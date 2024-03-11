package mission.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.User;
import mission.exception.ErrorCode;
import mission.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$") || requestUri.matches("^\\/swagger-ui(?:\\/.*)?$") || requestUri.matches("^\\/v3\\/api-docs(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        //request에서 AccessToken 헤더를 찾음
        String accessToken = request.getHeader("AccessToken");

        if (accessToken == null) {

            if(requestUri.matches("^\\/api\\/main(?:\\/.*)?$") || requestUri.matches("^\\/api\\/mission\\/info\\/.+")) {

                filterChain.doFilter(request, response);
            } else {

                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
            }

            return;
        }


        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED, ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());

            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            sendErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.ACCESS_TOKEN_INVALID, ErrorCode.ACCESS_TOKEN_INVALID.getMessage());

            return;
        }














        //Authorization 헤더 검증
//        if (accessToken == null) {
//            refreshToken = getRefreshToken(request);
//
//            if(refreshToken == null) {
//                sendErrorResponse(response, ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
//
//                //filterChain.doFilter(request, response);
//
//                return;
//            } else {
//                Boolean check = refreshTokenHandler(response, refreshToken);
//
//                if(!check) {
//
//                    jwtUtil.deleteRefreshToken(refreshToken);
//                    //sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "refresh_token_expired_or_invalid", "Refresh token expired or invalid.");
//
//                    sendErrorResponse(response, ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
//
//                    return;
//
//                }
//            }
//
//        } else{
//            JwtTokenValidationResult accessValidation = jwtUtil.tokenValidation(accessToken);
//
//            if(accessValidation == JwtTokenValidationResult.VALID) {
//                setAuthentication(accessToken);
//                System.out.println("accessToken good");
//            } else {
//                refreshToken = getRefreshToken(request);
//
//                if (refreshToken == null) {
//                    //sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "access_token_expired_or_invalid", "Access token has expired or invalid.");
//
//                    sendErrorResponse(response, ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
//
//                    return;
//                } else {
//                    Boolean check = refreshTokenHandler(response, refreshToken);
//
//                    if(!check) {
//                        System.out.println("refreshToken expired or invalid");
//
//                        jwtUtil.deleteRefreshToken(refreshToken);
//                        //sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "refresh_token_expired_or_invalid", "Refresh token expired or invalid.");
//
//                        sendErrorResponse(response, ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
//
//                        return;
//                    }
//                }
//            }
//        }

        setAuthentication(accessToken);


        filterChain.doFilter(request, response);
    }

    // JWT 토큰을 이용해 사용자 정보를 추출 후 보안 컨텍스트에 등록
    private void setAuthentication(String token) {
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        String email = jwtUtil.getEmail(token);

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(User.builder()
                .name(username)
                .role(role)
                .email(email)
                .build());

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    // JWT 관련 에러메세지 작성
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, ErrorCode errorCode, String errorMessage) throws IOException {
//        ResponseEntity<String> errorResponse = ResponseEntity.status(status)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body("{\"error\": \"" + error + "\", \"message\": \"" + message + "\"}");
//
//        response.setStatus(status.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write(errorResponse.getBody());

        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = ResponseEntity.status(status).body(errorResponse);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    // cookie에 존재하는 RefreshToken 가져오기
//    private String getRefreshToken(HttpServletRequest request) {
//        String refreshToken = null;
//
//        Cookie[] cookies = request.getCookies();
//
//        if(cookies != null) {
//            for (Cookie cookie : cookies) {
//
//                if (cookie.getName().equals("RefreshToken")) {
//
//                    refreshToken = cookie.getValue();
//                }
//            }
//            return refreshToken;
//        } else {
//            return null;
//        }
//    }

    // RefreshToken이 정상이고 만료되지 않았으면 새로운 AccessToken 발급 및 한번 사용된 RefreshToken 갱신
//    private Boolean refreshTokenHandler(HttpServletResponse response, String refreshToken) {
//        JwtTokenValidationResult refreshValidation = jwtUtil.refreshTokenValidation(refreshToken);
//
//        if(refreshValidation == JwtTokenValidationResult.VALID) {
//
//            String username = jwtUtil.getUsername(refreshToken);
//            String role = jwtUtil.getRole(refreshToken);
//            String email = jwtUtil.getEmail(refreshToken);
//
//            String newAccessToken = jwtUtil.createJwt(username, role, email, "Access");
//            String newRefreshToken = jwtUtil.createJwt(username, role, email, "Refresh");
//            jwtUtil.updateRefreshToken(newRefreshToken, email);
//
//            response.setHeader("AccessToken", newAccessToken);
//            response.addCookie(jwtUtil.createJwtCookie("RefreshToken", newRefreshToken));
//
//            setAuthentication(newAccessToken);
//            System.out.println("accessToken exchange");
//
//            return true;
//
//        } else {
//
//            return false;
//        }
//    }
}
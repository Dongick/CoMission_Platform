package mission.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.User;
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
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$") || requestUri.matches("^\\/swagger-ui(?:\\/.*)?$") || requestUri.matches("^\\/api\\/mission\\/search") ||
                requestUri.matches("^\\/v3\\/api-docs(?:\\/.*)?$") || requestUri.matches("^\\/api\\/reissue")) {

            filterChain.doFilter(request, response);
            return;
        }

        //request에서 AccessToken 헤더를 찾음
        String accessTokenHeader = request.getHeader("Authorization");

        if (accessTokenHeader == null) {
            if(requestUri.matches("^\\/api\\/main") || requestUri.matches("^\\/api\\/mission\\/info\\/.+")) {

                filterChain.doFilter(request, response);
            } else {
//                log.error("로그인을 하지 않아 해당 api에 접근 권한 없음");
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
            }

            return;
        }

        if(!accessTokenHeader.startsWith("Bearer ")) {
            log.error("AccessToken 값에 Bearer 포함 안됨");
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.ACCESS_TOKEN_INVALID, ErrorCode.ACCESS_TOKEN_INVALID.getMessage());

            return;
        }

        String accessToken = accessTokenHeader.substring(7);

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            log.warn("Access token 만료");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED, ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());

            return;
        } catch (SignatureException e) {
            log.error("Access token 만료여부 확인 불가");
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.ACCESS_TOKEN_INVALID, ErrorCode.ACCESS_TOKEN_INVALID.getMessage());

            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        try {
            String category = jwtUtil.getCategory(accessToken);

            if (!category.equals("access")) {
                log.error("Access token 페이로드 값이 access가 아님");
                sendErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.ACCESS_TOKEN_INVALID, ErrorCode.ACCESS_TOKEN_INVALID.getMessage());

                return;
            }

        } catch (SignatureException e) {
            log.error("Access token 페이로드 확인 불가");
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.ACCESS_TOKEN_INVALID, ErrorCode.ACCESS_TOKEN_INVALID.getMessage());

            return;
        }

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

        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = ResponseEntity.status(status).body(errorResponse);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }
}
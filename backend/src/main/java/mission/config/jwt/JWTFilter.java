package mission.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.dto.CustomOAuth2User;
import mission.dto.UserDto;
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

        if (requestUri.matches("^\\/login(?:\\/.*)?$") || requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = null;

        //request에서 AccessToken 헤더를 찾음
        String accessToken = request.getHeader("AccessToken");

        //Authorization 헤더 검증
        if (accessToken == null) {
            refreshToken = getRefreshToken(request);

            if(refreshToken == null) {
                System.out.println("token null");
                filterChain.doFilter(request, response);

                return;
            } else {
                Boolean check = refreshTokenHandler(response, refreshToken);

                if(!check) {
                    System.out.println("refreshToken expired or invalid");

                    jwtUtil.deleteRefreshToken(refreshToken);
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "refresh_token_expired_or_invalid", "Refresh token expired or invalid.");

                    return;
                }
            }

        } else{
            JwtTokenValidationResult accessValidation = jwtUtil.tokenValidation(accessToken);

            if(accessValidation == JwtTokenValidationResult.VALID) {
                setAuthentication(accessToken);
                System.out.println("accessToken good");
            } else {
                refreshToken = getRefreshToken(request);

                if (refreshToken == null) {
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "access_token_expired_or_invalid", "Access token has expired or invalid.");
                    System.out.println("access token expired or invalid");

                    return;
                } else {
                    Boolean check = refreshTokenHandler(response, refreshToken);

                    if(!check) {
                        System.out.println("refreshToken expired or invalid");

                        jwtUtil.deleteRefreshToken(refreshToken);
                        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "refresh_token_expired_or_invalid", "Refresh token expired or invalid.");

                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        String email = jwtUtil.getEmail(token);

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(UserDto.builder()
                .name(username)
                .role(role)
                .email(email)
                .build());

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
        ResponseEntity<String> errorResponse = ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"" + error + "\", \"message\": \"" + message + "\"}");

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(errorResponse.getBody());
    }

    private String getRefreshToken(HttpServletRequest request) {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("RefreshToken")) {

                    refreshToken = cookie.getValue();
                }
            }
            return refreshToken;
        } else {
            return null;
        }
    }

    private Boolean refreshTokenHandler(HttpServletResponse response, String refreshToken) {
        JwtTokenValidationResult refreshValidation = jwtUtil.refreshTokenValidation(refreshToken);

        if(refreshValidation == JwtTokenValidationResult.VALID) {

            String username = jwtUtil.getUsername(refreshToken);
            String role = jwtUtil.getRole(refreshToken);
            String email = jwtUtil.getEmail(refreshToken);

            String newAccessToken = jwtUtil.createJwt(username, role, email, "Access");
            String newRefreshToken = jwtUtil.createJwt(username, role, email, "Refresh");
            jwtUtil.updateRefreshToken(newRefreshToken, email);

            response.setHeader("AccessToken", newAccessToken);
            response.addCookie(jwtUtil.createJwtCookie("RefreshToken", newRefreshToken));

            setAuthentication(newAccessToken);
            System.out.println("accessToken exchange");

            return true;

        } else {

            return false;
        }
    }
}
package mission.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.dto.CustomOAuth2User;
import mission.dto.UserDto;
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

        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if(cookies == null) {
            System.out.println("no cookies");
            filterChain.doFilter(request, response);
            return;
        }

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("AccessToken")) {

                accessToken = cookie.getValue();
            } else if(cookie.getName().equals("RefreshToken")) {

                refreshToken = cookie.getValue();
            }
        }

        //Authorization 헤더 검증
        if (accessToken == null) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            return;
        } else{
            if(refreshToken == null) {

                //토큰 소멸 시간 검증
                if (!jwtUtil.tokenValidation(accessToken)) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"access_token_expired\", \"message\": \"Access token has expired.\"}");

                    System.out.println("token expired");

                    return;
                } else{
                    setAuthentication(accessToken);
                    System.out.println("accessToken good");
                }
            } else{
                if(jwtUtil.refreshTokenValidation(refreshToken)) {

                    String username = jwtUtil.getUsername(refreshToken);
                    String role = jwtUtil.getRole(refreshToken);
                    String email = jwtUtil.getEmail(refreshToken);

                    String newAccessToken = jwtUtil.createJwt(username, role, email, "Access");

                    Cookie cookie = new Cookie("AccessToken", newAccessToken);
                    cookie.setMaxAge(60*60*60);
                    //cookie.setSecure(true);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);

                    response.addCookie(cookie);

                    setAuthentication(refreshToken);
                    System.out.println("accessToken exchange");

                } else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"refreshToken error\", \"message\": \"refreshToken error has expired.\"}");

                    System.out.println("refreshToken error");

                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String token) {
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
}
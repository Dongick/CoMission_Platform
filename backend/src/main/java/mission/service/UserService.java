package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import mission.dto.oauth2.CustomOAuth2User;
=======
import mission.config.jwt.JWTUtil;
import mission.dto.oauth2.CustomOAuth2User;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import mission.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
<<<<<<< HEAD
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void logout() {
=======
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

<<<<<<< HEAD
        refreshTokenRepository.deleteByEmail(userEmail);
=======
        // cookie에서 refresh token 찾음
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("RefreshToken")) {

                    refresh = cookie.getValue();
                }
            }
        } else {
            throw new MissionAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
        }

        // refreshToken 검증
        jwtUtil.validateRefreshToken(refresh);

        // db에서 refreshToken 삭제
        refreshTokenRepository.deleteByEmail(userEmail);

        // 쿠키에서 refreshToken 삭제
        Cookie cookie = new Cookie("RefreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
    }
}

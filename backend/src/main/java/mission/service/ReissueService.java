package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.config.jwt.JWTUtil;
import mission.entity.RefreshTokenEntity;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
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
        RefreshTokenEntity refreshTokenEntity = jwtUtil.validateRefreshToken(refresh);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String email = jwtUtil.getEmail(refresh);

        String newAccessToken = jwtUtil.createJwt("access", username, role, email);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, email);

        jwtUtil.updateRefreshToken(refreshTokenEntity, newRefreshToken);

        response.setHeader("Authorization", newAccessToken);
        response.addCookie(jwtUtil.createJwtCookie("RefreshToken", newRefreshToken));
    }
}

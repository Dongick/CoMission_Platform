package mission.service;

<<<<<<< HEAD
import io.jsonwebtoken.ExpiredJwtException;
=======
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.config.jwt.JWTUtil;
import mission.entity.RefreshTokenEntity;
<<<<<<< HEAD
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import mission.repository.RefreshTokenRepository;
=======
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
<<<<<<< HEAD
    private final RefreshTokenRepository refreshTokenRepository;

=======
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        // cookie에서 refresh token 찾음
        String refresh = null;
        Cookie[] cookies = request.getCookies();
<<<<<<< HEAD
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("RefreshToken")) {

                refresh = cookie.getValue();
            }
        }

        // 토큰이 존재하는지 확인
        if (refresh == null) {

            throw new MissionAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
        }

        System.out.println(refresh);

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            throw new MissionAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED, ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
        }

        //DB에 저장되어 있는지 확인
        Optional<RefreshTokenEntity> optionalRefreshTokenEntity = refreshTokenRepository.findByRefreshToken(refresh);
        if (optionalRefreshTokenEntity.isEmpty()) {

            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
        }
=======
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
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String email = jwtUtil.getEmail(refresh);

        String newAccessToken = jwtUtil.createJwt("access", username, role, email);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, email);

<<<<<<< HEAD
        jwtUtil.updateRefreshToken(optionalRefreshTokenEntity.get(), newRefreshToken);
=======
        jwtUtil.updateRefreshToken(refreshTokenEntity, newRefreshToken);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        response.setHeader("AccessToken", newAccessToken);
        response.addCookie(jwtUtil.createJwtCookie("RefreshToken", newRefreshToken));
    }
}

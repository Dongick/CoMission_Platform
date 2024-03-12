package mission.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.config.jwt.JWTUtil;
import mission.entity.RefreshTokenEntity;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import mission.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        // cookie에서 refresh token 찾음
        String refresh = null;
        Cookie[] cookies = request.getCookies();
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

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String email = jwtUtil.getEmail(refresh);

        String newAccessToken = jwtUtil.createJwt("access", username, role, email);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, email);

        jwtUtil.updateRefreshToken(optionalRefreshTokenEntity.get(), newRefreshToken);

        response.setHeader("AccessToken", newAccessToken);
        response.addCookie(jwtUtil.createJwtCookie("RefreshToken", newRefreshToken));
    }
}

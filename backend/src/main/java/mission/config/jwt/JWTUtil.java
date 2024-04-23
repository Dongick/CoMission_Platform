package mission.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import mission.entity.RefreshTokenEntity;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import mission.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long ACCESS_TIME = 30 * 60 * 1000L;
    private static final long REFRESH_TIME = 24 * 60 * 60 * 1000L;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret, RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 토큰에서 username 추출
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // JWT 토큰에서 권한 추출
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // JWT 토큰에서 email 추출
    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    // JWT 토큰에서 해당 토큰이 access인지 refresh인지 추출
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // JWT 토큰 인증 메서드
        public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // DB에 존재하는 한번 사용된 RefreshToken 갱신 메서드
    public void updateRefreshToken(RefreshTokenEntity refreshTokenEntity, String refreshToken) {
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setExpiration((new Date(System.currentTimeMillis() + REFRESH_TIME)).toString());

        refreshTokenRepository.save(refreshTokenEntity);
    }

    // RefreshToken db 저장 메서드
    public void saveRefreshToken(String refreshToken, String email) {

        Date date = new Date(System.currentTimeMillis() + REFRESH_TIME);

        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .refreshToken(refreshToken)
                .email(email)
                .expiration(date.toString())
                .build());
    }

    // RefreshToken cookie 생성 메서드
    public Cookie createJwtCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//        cookie.setAttribute("SameSite", "None");

        return cookie;
    }

    // JWT 토큰 생성 메서드
    public String createJwt(String category, String username, String role, String email) {

        long time = category.equals("access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + time))
                .signWith(secretKey)
                .compact();
    }

    public RefreshTokenEntity validateRefreshToken(String refreshToken) {

        // 토큰이 존재하는지 확인
        if (refreshToken == null) {

            throw new MissionAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
        }

        // 토큰 만료 여부 확인
        try {
            isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            throw new MissionAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED, ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = getCategory(refreshToken);

        if (!category.equals("refresh")) {

            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
        }

        //DB에 저장되어 있는지 확인
        Optional<RefreshTokenEntity> optionalRefreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (optionalRefreshTokenEntity.isEmpty()) {

            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage());
        }

        return optionalRefreshTokenEntity.get();
    }
}

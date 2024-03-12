package mission.config.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import mission.entity.RefreshTokenEntity;
import mission.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // JWT 토큰 인증 메서드
        public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // RefreshToken 추가 인증 메서드
//    public JwtTokenValidationResult refreshTokenValidation(String token) {
//
//        JwtTokenValidationResult validation = tokenValidation(token);
//
//        if(validation == JwtTokenValidationResult.VALID) {
//            Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokenRepository.findByEmail(getEmail(token));
//
//            if(refreshTokenEntity.isPresent() && token.equals(refreshTokenEntity.get().getRefreshToken())) {
//                return JwtTokenValidationResult.VALID;
//            } else {
//                return JwtTokenValidationResult.REFRESH_TOKEN_DB_MISMATCH;
//            }
//        } else {
//            return validation;
//        }
//    }

    // DB에 존재하는 한번 사용된 RefreshToken 갱신 메서드
    @Transactional
    public void updateRefreshToken(RefreshTokenEntity refreshTokenEntity, String refreshToken) {
        refreshTokenEntity.setRefreshToken(refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    // RefreshToken cookie 생성 메서드
    public Cookie createJwtCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

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
}

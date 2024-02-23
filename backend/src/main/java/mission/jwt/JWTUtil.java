package mission.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import mission.entity.RefreshTokenEntity;
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
    private static final long ACCESS_TIME = 60 * 1000L;
    private static final long REFRESH_TIME = 3 * 60 * 1000L;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret, RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public Boolean tokenValidation(String token) {

        try{
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch(SignatureException e) {
            return false;
        } catch(JwtException e) {
            return false;
        }
    }

    public Boolean refreshTokenValidation(String token) {

        if(!tokenValidation(token)){
            return false;
        }
        Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokenRepository.findByEmail(getEmail(token));

        return refreshTokenEntity.isPresent() && token.equals(refreshTokenEntity.get().getRefreshToken());
    }

    public String createJwt(String username, String role, String email, String type) {

        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + time))
                .signWith(secretKey)
                .compact();
    }
}

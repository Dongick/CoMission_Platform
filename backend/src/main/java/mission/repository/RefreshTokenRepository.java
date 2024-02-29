package mission.repository;

import mission.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByEmail(String email);
    Long deleteByEmail(String email);

    Long deleteByRefreshToken(String refreshToken);
}

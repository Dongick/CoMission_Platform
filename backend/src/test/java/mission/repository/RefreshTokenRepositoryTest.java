package mission.repository;

import mission.entity.RefreshTokenEntity;
import org.assertj.core.api.Assertions;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.CurrentTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void tearDown() {
        refreshTokenRepository.deleteAll(); // 테스트가 시작 전 모든 데이터 삭제
    }

    @Test
    void findByEmail() {
        //given
        Long refreshTokenTime = 24 * 60 * 60 * 1000L;

        String refreshToken1 = "test1";
        String refreshToken2 = "test2";
        String refreshToken3 = "test3";

        String email1 = "test1@example.com";
        String email2 = "test2@example.com";
        String email3 = "test3@example.com";
        String email4 = "test4@example.com";

        RefreshTokenEntity refreshTokenEntity1 = RefreshTokenEntity.builder()
                .id(1L)
                .email(email1)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                .refreshToken(refreshToken1)
                .build();
        refreshTokenRepository.save(refreshTokenEntity1);

        RefreshTokenEntity refreshTokenEntity2 = RefreshTokenEntity.builder()
                .id(2L)
                .email(email2)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                .refreshToken(refreshToken2)
                .build();
        refreshTokenRepository.save(refreshTokenEntity2);

        RefreshTokenEntity refreshTokenEntity3 = RefreshTokenEntity.builder()
                .id(3L)
                .email(email3)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                .refreshToken(refreshToken3)
                .build();
        refreshTokenRepository.save(refreshTokenEntity3);

        //when
        Optional<RefreshTokenEntity> refreshTokenEntityOptional1 = refreshTokenRepository.findByEmail(email1);
        Optional<RefreshTokenEntity> refreshTokenEntityOptional2 = refreshTokenRepository.findByEmail(email2);
        Optional<RefreshTokenEntity> refreshTokenEntityOptional3 = refreshTokenRepository.findByEmail(email3);
        Optional<RefreshTokenEntity> refreshTokenEntityOptional4 = refreshTokenRepository.findByEmail(email4);

        //then
        Assertions.assertThat(refreshTokenEntityOptional1.isPresent());
        Assertions.assertThat(refreshTokenEntityOptional1.get().getRefreshToken()).isEqualTo(refreshToken1);

        Assertions.assertThat(refreshTokenEntityOptional2.isPresent());
        Assertions.assertThat(refreshTokenEntityOptional2.get().getRefreshToken()).isEqualTo(refreshToken2);

        Assertions.assertThat(refreshTokenEntityOptional3.isPresent());
        Assertions.assertThat(refreshTokenEntityOptional3.get().getRefreshToken()).isEqualTo(refreshToken3);

        Assertions.assertThat(refreshTokenEntityOptional4.isEmpty());
    }

    @Test
    void deleteByEmail() {
        //given
        Long refreshTokenTime = 24 * 60 * 60 * 1000L;

        String refreshToken1 = "test1";
        String refreshToken2 = "test2";
        String refreshToken3 = "test3";

        String email1 = "test1@example.com";
        String email2 = "test2@example.com";
        String email3 = "test3@example.com";

        RefreshTokenEntity refreshTokenEntity1 = RefreshTokenEntity.builder()
                .id(1L)
                .email(email1)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                .refreshToken(refreshToken1)
                .build();
        refreshTokenRepository.save(refreshTokenEntity1);

        RefreshTokenEntity refreshTokenEntity2 = RefreshTokenEntity.builder()
                .id(2L)
                .email(email2)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                .refreshToken(refreshToken2)
                .build();
        refreshTokenRepository.save(refreshTokenEntity2);

        RefreshTokenEntity refreshTokenEntity3 = RefreshTokenEntity.builder()
                .id(3L)
                .email(email3)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                .refreshToken(refreshToken3)
                .build();
        refreshTokenRepository.save(refreshTokenEntity3);

        //when
    }

    @Test
    void findByRefreshToken() {
    }
}
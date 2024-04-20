package mission.repository;

import mission.entity.RefreshTokenEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;
import java.util.List;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private static final Long refreshTokenTime = 24 * 60 * 60 * 1000L;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();

        String[] refreshTokens = {"test1", "test2", "test3"};
        String[] emails = {"test1@example.com", "test2@example.com", "test3@example.com"};

        for (int i = 0; i < refreshTokens.length; i++) {
            RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                    .id((long) (i + 1))
                    .email(emails[i])
                    .expiration(new Date(System.currentTimeMillis() + refreshTokenTime).toString())
                    .refreshToken(refreshTokens[i])
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);
        }
    }

    @Test
    @DisplayName("RefreshTokenRepository의 findByEmail 매서드 테스트")
    void findByEmail() {
        //given
        String refreshToken1 = "test1";
        String refreshToken2 = "test2";
        String refreshToken3 = "test3";

        String email1 = "test1@example.com";
        String email2 = "test2@example.com";
        String email3 = "test3@example.com";
        String email4 = "test4@example.com";

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
    @DisplayName("RefreshTokenRepository의 deleteByEmail 매서드 테스트")
    void deleteByEmail() {
        //given
        String refreshToken2 = "test2";
        String refreshToken3 = "test3";

        String email1 = "test1@example.com";

        //when
        refreshTokenRepository.deleteByEmail(email1);

        List<RefreshTokenEntity> refreshTokenEntityList = refreshTokenRepository.findAll();

        //then
        Assertions.assertThat(refreshTokenEntityList.size()).isEqualTo(2);

        Assertions.assertThat(refreshTokenEntityList.get(0).getRefreshToken()).isEqualTo(refreshToken2);
        Assertions.assertThat(refreshTokenEntityList.get(1).getRefreshToken()).isEqualTo(refreshToken3);
    }

    @Test
    @DisplayName("RefreshTokenRepository의 findByRefreshToken 매서드 테스트")
    void findByRefreshToken() {
        //given
        String refreshToken1 = "test1";
        String refreshToken2 = "test2";
        String refreshToken3 = "test3";
        String refreshToken4 = "test4";

        //when
        Optional<RefreshTokenEntity> refreshTokenEntityOptional1 = refreshTokenRepository.findByRefreshToken(refreshToken1);
        Optional<RefreshTokenEntity> refreshTokenEntityOptional2 = refreshTokenRepository.findByRefreshToken(refreshToken2);
        Optional<RefreshTokenEntity> refreshTokenEntityOptional3 = refreshTokenRepository.findByRefreshToken(refreshToken3);
        Optional<RefreshTokenEntity> refreshTokenEntityOptional4 = refreshTokenRepository.findByRefreshToken(refreshToken4);

        //then
        Assertions.assertThat(refreshTokenEntityOptional1.isPresent());
        Assertions.assertThat(refreshTokenEntityOptional1.get().getRefreshToken()).isEqualTo(refreshToken1);

        Assertions.assertThat(refreshTokenEntityOptional2.isPresent());
        Assertions.assertThat(refreshTokenEntityOptional2.get().getRefreshToken()).isEqualTo(refreshToken2);

        Assertions.assertThat(refreshTokenEntityOptional3.isPresent());
        Assertions.assertThat(refreshTokenEntityOptional3.get().getRefreshToken()).isEqualTo(refreshToken3);

        Assertions.assertThat(refreshTokenEntityOptional4.isEmpty());
    }
}
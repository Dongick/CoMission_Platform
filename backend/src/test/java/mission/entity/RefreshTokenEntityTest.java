package mission.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenEntityTest {

    @Test
    @DisplayName("refreshToken 생성되는지 확인하는 테스트")
    void createRefreshToken() {

        // given
        Long id = 1L;
        String email = "dlehddlr3219@naver.com";
        String refreshToken = "dnnioe21e2031enne";
        String expiration = "2024-3-31";

        // when
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(id)
                .refreshToken(refreshToken)
                .expiration(expiration)
                .email(email)
                .build();

        // then
        Assertions.assertThat(refreshTokenEntity.getId()).isEqualTo(id);
        Assertions.assertThat(refreshTokenEntity.getEmail()).isEqualTo(email);
        Assertions.assertThat(refreshTokenEntity.getRefreshToken()).isEqualTo(refreshToken);
        Assertions.assertThat(refreshTokenEntity.getExpiration()).isEqualTo(expiration);
    }
}
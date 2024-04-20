package mission.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    @DisplayName("User entity가 생성되는지 확인하는 테스트")
    void createUserEntity() {

        // given
        Long id = 1L;
        String email = "dlehddlr3219@naver.com";
        String username = "이동익";
        String role = "USER";

        // when
        UserEntity userEntity = UserEntity.builder()
                .id(id)
                .role(role)
                .email(email)
                .username(username)
                .build();

        // then
        Assertions.assertThat(userEntity.getId()).isEqualTo(id);
        Assertions.assertThat(userEntity.getEmail()).isEqualTo(email);
        Assertions.assertThat(userEntity.getRole()).isEqualTo(role);
        Assertions.assertThat(userEntity.getUsername()).isEqualTo(username);
    }
}
package mission.repository;

import mission.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("UserRepository의 findByEmail 매서드 테스트")
    void findByEmail() {

        //given
        String email1 = "test1@example.com";
        String email2 = "test2@example.com";
        String username = "testUser";
        String role = "USER";

        UserEntity userEntity = UserEntity.builder()
                .email(email1)
                .username(username)
                .role(role)
                .build();

        userRepository.save(userEntity);

        //when
        Optional<UserEntity> userEntityOptional1 = userRepository.findByEmail(email1);
        Optional<UserEntity> userEntityOptional2 = userRepository.findByEmail(email2);

        //then
        Assertions.assertThat(userEntityOptional1.isPresent());
        Assertions.assertThat(email1).isEqualTo(userEntityOptional1.get().getEmail());

        Assertions.assertThat(userEntityOptional2.isEmpty());
    }
}
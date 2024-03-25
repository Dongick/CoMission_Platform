package mission.repository;

import mission.entity.UserEntity;
import org.assertj.core.api.Assertions;
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
    @Test
    @DisplayName("해당 사용자가 존재할 때 email로 해당 사용자를 찾을 수 있는지 테스트")
    void testFindByEmail_Exist() {

        //given
        String email = "test@example.com";
        String username = "testUser";
        String role = "USER";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .username(username)
                .role(role)
                .build();

        userRepository.save(userEntity);

        //when
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        //then
        Assertions.assertThat(userEntityOptional.isPresent());
        Assertions.assertThat(email).isEqualTo(userEntityOptional.get().getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 이메일을 가진 사용자를 찾을 때 빈 Optional을 반환하는지 확인하는 테스트")
    void testFindByEmail_NonExist() {

        //given
        String email = "test@example.com";

        //when
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        //then
        Assertions.assertThat(userEntityOptional.isEmpty());
    }
}
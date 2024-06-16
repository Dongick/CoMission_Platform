package mission.document;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class AuthenticationTest {

    @Test
    @DisplayName("Authentication 객체가 생성되는지 확인하는 테스트")
    void createAuthentication() {
        // Given
        LocalDateTime date = LocalDateTime.now();
        boolean completed = true;
        String photoData = "example.jpg";
        String textData = "/test/1";

        // When
        AuthenticationDocument authentication = AuthenticationDocument.builder()
                .date(date)
                .completed(completed)
                .photoData(photoData)
                .textData(textData)
                .build();

        // Then
        Assertions.assertThat(authentication.getDate()).isEqualTo(date);
        Assertions.assertThat(authentication.getPhotoData()).isEqualTo(photoData);
        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
        Assertions.assertThat(authentication.isCompleted()).isEqualTo(completed);

    }
}
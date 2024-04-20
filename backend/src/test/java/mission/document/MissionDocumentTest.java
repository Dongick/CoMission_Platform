package mission.document;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MissionDocumentTest {

    @Test
    @DisplayName("Mission document가 생성되는지 확인하는 테스트")
    void createMissionDocument() {
        // Given
        ObjectId id = new ObjectId("616157d8d4e12e67c17dc154");
        LocalDateTime createdAt = LocalDateTime.now();
        String title = "Test Mission";
        String description = "This is a test mission.";
        int minParticipants = 1;
        int participants = 1;
        int duration = 10;
        LocalDate startDate = LocalDate.from(createdAt);
        LocalDate deadline = startDate.plusDays(duration);
        String status = "CREATED";
        String frequency = "주2회";
        String creatorEmail = "test@example.com";

        // When
        MissionDocument missionDocument = MissionDocument.builder()
                .id(id)
                .createdAt(createdAt)
                .startDate(startDate)
                .deadline(deadline)
                .title(title)
                .description(description)
                .minParticipants(minParticipants)
                .participants(participants)
                .duration(duration)
                .status(status)
                .frequency(frequency)
                .creatorEmail(creatorEmail)
                .build();

        // Then
        Assertions.assertThat(missionDocument.getId()).isEqualTo(id);
        Assertions.assertThat(missionDocument.getCreatedAt()).isEqualTo(createdAt);
        Assertions.assertThat(missionDocument.getCreatorEmail()).isEqualTo(creatorEmail);
        Assertions.assertThat(missionDocument.getFrequency()).isEqualTo(frequency);
        Assertions.assertThat(missionDocument.getDuration()).isEqualTo(duration);
        Assertions.assertThat(missionDocument.getDescription()).isEqualTo(description);
        Assertions.assertThat(missionDocument.getDeadline()).isEqualTo(deadline);
        Assertions.assertThat(missionDocument.getParticipants()).isEqualTo(participants);
        Assertions.assertThat(missionDocument.getStatus()).isEqualTo(status);
        Assertions.assertThat(missionDocument.getTitle()).isEqualTo(title);
        Assertions.assertThat(missionDocument.getStartDate()).isEqualTo(startDate);
        Assertions.assertThat(missionDocument.getMinParticipants()).isEqualTo(minParticipants);
    }
}
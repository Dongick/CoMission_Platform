package mission.document;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantDocumentTest {
    @Test
    @DisplayName("Participant document가 생성되는지 확인하는 테스트")
    void createParticipantDocument() {
        // Given
        ObjectId id = new ObjectId("616157d8d4e12e67c17dc154");
        LocalDateTime joinedAt = LocalDateTime.now();
        ObjectId missionId = new ObjectId("616157d8d4e12e67c17dc15d");
        String userEmail = "test@example.com";
        List<Authentication> authenticationList = new ArrayList<>();

        // When
        ParticipantDocument participantDocument = ParticipantDocument.builder()
                .id(id)
                .joinedAt(joinedAt)
                .missionId(missionId)
                .userEmail(userEmail)
                .authentication(authenticationList)
                .build();

        // Then
        Assertions.assertThat(participantDocument.getId()).isEqualTo(id);
        Assertions.assertThat(participantDocument.getJoinedAt()).isEqualTo(joinedAt);
        Assertions.assertThat(participantDocument.getUserEmail()).isEqualTo(userEmail);
        Assertions.assertThat(participantDocument.getAuthentication()).isEqualTo(authenticationList);
        Assertions.assertThat(participantDocument.getMissionId()).isEqualTo(missionId);
    }
}
package mission.repository;

import mission.document.ParticipantDocument;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataMongoTest
class ParticipantRepositoryTest {

    @Autowired
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        participantRepository.deleteAll();

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};
        ObjectId[] missionId = {new ObjectId("65ea0c8007b2c737d6227ba0"), new ObjectId("65ea0c8007b2c737d6227ba2"), new ObjectId("65ea0c8007b2c737d6227ba4")};
        String[] username = {"test1", "test2", "test3"};
        String[] userEmail = {"test1@example.com", "test2@example.com", "test3@example.com"};

        for (int i = 0; i < ids.length; i++) {
            ParticipantDocument participantDocument = ParticipantDocument.builder()
                    .id(ids[i])
                    .username(username[i])
//                    .authentication(new ArrayList<>())
                    .missionId(missionId[i])
                    .joinedAt(LocalDateTime.now())
                    .userEmail(userEmail[i])
                    .build();
            participantRepository.save(participantDocument);
        }
    }

    @Test
    @DisplayName("ParticipantRepository의 findByUserEmail 매서드 테스트")
    void findByUserEmail() {
        //given
        ObjectId[] missionId = {new ObjectId("65ea0c8007b2c737d6227ba0"), new ObjectId("65ea0c8007b2c737d6227ba2"), new ObjectId("65ea0c8007b2c737d6227ba4"), new ObjectId("65ea0c8007b2c737d6227ba6")};
        String[] userEmail = {"test1@example.com", "test2@example.com", "test3@example.com", "test4@example.com"};

        ParticipantDocument participantDocument = ParticipantDocument.builder()
                .id(new ObjectId("65ea0c8007b2c737d6227bf6"))
                .username("test1")
//                .authentication(new ArrayList<>())
                .missionId(missionId[3])
                .joinedAt(LocalDateTime.now())
                .userEmail(userEmail[0])
                .build();
        participantRepository.save(participantDocument);

        //when
        List<ParticipantDocument> participantDocumentList1 = participantRepository.findByUserEmail(userEmail[0]);
        List<ParticipantDocument> participantDocumentList2 = participantRepository.findByUserEmail(userEmail[1]);
        List<ParticipantDocument> participantDocumentList3 = participantRepository.findByUserEmail(userEmail[2]);
        List<ParticipantDocument> participantDocumentList4 = participantRepository.findByUserEmail(userEmail[3]);

        //then
        Assertions.assertThat(participantDocumentList1.size()).isEqualTo(2);
        Assertions.assertThat(participantDocumentList1.get(0).getMissionId()).isEqualTo(missionId[0]);
        Assertions.assertThat(participantDocumentList1.get(1).getMissionId()).isEqualTo(missionId[3]);

        Assertions.assertThat(participantDocumentList2.size()).isEqualTo(1);
        Assertions.assertThat(participantDocumentList2.get(0).getMissionId()).isEqualTo(missionId[1]);

        Assertions.assertThat(participantDocumentList3.size()).isEqualTo(1);
        Assertions.assertThat(participantDocumentList3.get(0).getMissionId()).isEqualTo(missionId[2]);

        Assertions.assertThat(participantDocumentList4.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("ParticipantRepository의 findByMissionId 매서드 테스트")
    void findByMissionId() {
        //given
        ObjectId[] missionId = {new ObjectId("65ea0c8007b2c737d6227ba0"), new ObjectId("65ea0c8007b2c737d6227ba2"), new ObjectId("65ea0c8007b2c737d6227ba4"), new ObjectId("65ea0c8007b2c737d6227ba6")};
        String[] userEmail = {"test1@example.com", "test2@example.com", "test3@example.com"};
        String[] username = {"test1", "test2", "test3"};

        ParticipantDocument participantDocument = ParticipantDocument.builder()
                .id(new ObjectId("65ea0c8007b2c737d6227bf6"))
                .username(username[1])
//                .authentication(new ArrayList<>())
                .missionId(missionId[0])
                .joinedAt(LocalDateTime.now())
                .userEmail(userEmail[1])
                .build();
        participantRepository.save(participantDocument);

        //when
        List<ParticipantDocument> participantDocumentList1 = participantRepository.findByMissionId(missionId[0]);
        List<ParticipantDocument> participantDocumentList2 = participantRepository.findByMissionId(missionId[1]);
        List<ParticipantDocument> participantDocumentList3 = participantRepository.findByMissionId(missionId[2]);
        List<ParticipantDocument> participantDocumentList4 = participantRepository.findByMissionId(missionId[3]);

        //then
        Assertions.assertThat(participantDocumentList1.size()).isEqualTo(2);
        Assertions.assertThat(participantDocumentList1.get(0).getUserEmail()).isEqualTo(userEmail[0]);
        Assertions.assertThat(participantDocumentList1.get(1).getUserEmail()).isEqualTo(userEmail[1]);

        Assertions.assertThat(participantDocumentList2.size()).isEqualTo(1);
        Assertions.assertThat(participantDocumentList2.get(0).getUserEmail()).isEqualTo(userEmail[1]);

        Assertions.assertThat(participantDocumentList3.size()).isEqualTo(1);
        Assertions.assertThat(participantDocumentList3.get(0).getUserEmail()).isEqualTo(userEmail[2]);

        Assertions.assertThat(participantDocumentList4.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("ParticipantRepository의 findByMissionIdAndUserEmail 매서드 테스트")
    void findByMissionIdAndUserEmail() {
        //given
        ObjectId[] missionId = {new ObjectId("65ea0c8007b2c737d6227ba0"), new ObjectId("65ea0c8007b2c737d6227ba2"), new ObjectId("65ea0c8007b2c737d6227ba4")};
        String[] userEmail = {"test1@example.com", "test2@example.com", "test3@example.com", "test4@example.com"};
        String[] username = {"test1", "test2", "test3"};

        //when
        Optional<ParticipantDocument> participantDocumentOptional1 = participantRepository.findByMissionIdAndUserEmail(missionId[0], userEmail[0]);
        Optional<ParticipantDocument> participantDocumentOptional2 = participantRepository.findByMissionIdAndUserEmail(missionId[1], userEmail[1]);
        Optional<ParticipantDocument> participantDocumentOptional3 = participantRepository.findByMissionIdAndUserEmail(missionId[2], userEmail[2]);
        Optional<ParticipantDocument> participantDocumentOptional4 = participantRepository.findByMissionIdAndUserEmail(missionId[2], userEmail[3]);

        //then
        Assertions.assertThat(participantDocumentOptional1).isPresent();
        Assertions.assertThat(participantDocumentOptional1.get().getUsername()).isEqualTo(username[0]);

        Assertions.assertThat(participantDocumentOptional2).isPresent();
        Assertions.assertThat(participantDocumentOptional2.get().getUsername()).isEqualTo(username[1]);

        Assertions.assertThat(participantDocumentOptional3).isPresent();
        Assertions.assertThat(participantDocumentOptional3.get().getUsername()).isEqualTo(username[2]);

        Assertions.assertThat(participantDocumentOptional4).isEmpty();

    }
}
package mission.repository;

import mission.document.AuthenticationDocument;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class AuthenticationRepositoryTest {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @BeforeEach
    void setUp() {
        authenticationRepository.deleteAll();

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4"),
                new ObjectId("65ea0c8007b2c737d6227bf6"), new ObjectId("65ea0c8007b2c737d6227bf8"), new ObjectId("65ea0c8007b2c737d6227bfa")};
        ObjectId missionId = new ObjectId("55ea0c8007b2c737d6227bf0");
        ObjectId[] participantId = {new ObjectId("45ea0c8007b2c737d6227bf0"), new ObjectId("45ea0c8007b2c737d6227bf2")};
        String[] textData = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        String[] username = {"test1", "test2"};
        String[] userEmail = {"test1@example.com", "test2@example.com"};
        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        for (int i = 0; i < ids.length; i++) {
            AuthenticationDocument authenticationDocument = AuthenticationDocument.builder()
                    .id(ids[i])
                    .missionId(missionId)
                    .participantId(participantId[i % 2])
                    .userEmail(userEmail[i % 2])
                    .username(username[i % 2])
                    .photoData(null)
                    .textData(textData[i])
                    .date(now.plusDays(i))
                    .completed(true)
                    .build();
            authenticationRepository.save(authenticationDocument);
        }
    }

    @Test
    @DisplayName("AuthenticationRepository의 findAll 매서드 테스트")
    void findAll() {

        String[] textData = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "date"));
        Pageable pageable2 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "date"));
        Pageable pageable3 = PageRequest.of(1, 4, Sort.by(Sort.Direction.DESC, "date"));

        Page<AuthenticationDocument> authenticationDocumentPage1 = authenticationRepository.findAll(pageable1);
        Page<AuthenticationDocument> authenticationDocumentPage2 = authenticationRepository.findAll(pageable2);
        Page<AuthenticationDocument> authenticationDocumentPage3 = authenticationRepository.findAll(pageable3);

        List<AuthenticationDocument> authenticationDocumentList1 = authenticationDocumentPage1.getContent();
        List<AuthenticationDocument> authenticationDocumentList2 = authenticationDocumentPage2.getContent();
        List<AuthenticationDocument> authenticationDocumentList3 = authenticationDocumentPage3.getContent();

        Assertions.assertThat(authenticationDocumentList1.size()).isEqualTo(6);
        Assertions.assertThat(authenticationDocumentList1.get(0).getTextData()).isEqualTo(textData[5]);
        Assertions.assertThat(authenticationDocumentList1.get(1).getTextData()).isEqualTo(textData[4]);
        Assertions.assertThat(authenticationDocumentList1.get(5).getTextData()).isEqualTo(textData[0]);

        Assertions.assertThat(authenticationDocumentList2.size()).isEqualTo(5);
        Assertions.assertThat(authenticationDocumentList2.get(0).getTextData()).isEqualTo(textData[5]);
        Assertions.assertThat(authenticationDocumentList2.get(1).getTextData()).isEqualTo(textData[4]);
        Assertions.assertThat(authenticationDocumentList2.get(4).getTextData()).isEqualTo(textData[1]);

        Assertions.assertThat(authenticationDocumentList3.size()).isEqualTo(2);
        Assertions.assertThat(authenticationDocumentList3.get(0).getTextData()).isEqualTo(textData[1]);
        Assertions.assertThat(authenticationDocumentList3.get(1).getTextData()).isEqualTo(textData[0]);
    }

    @Test
    @DisplayName("AuthenticationRepository의 findByParticipantIdAndMissionId 매서드 테스트")
    void findByParticipantIdAndMissionId() {
        String[] textData = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        ObjectId missionId = new ObjectId("55ea0c8007b2c737d6227bf0");
        ObjectId[] participantId = {new ObjectId("45ea0c8007b2c737d6227bf0"), new ObjectId("45ea0c8007b2c737d6227bf2")};

        Pageable pageable1 = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "date"));
        Pageable pageable2 = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "date"));

        Page<AuthenticationDocument> authenticationDocumentPage1 = authenticationRepository.findByParticipantIdAndMissionId(participantId[0], missionId, pageable1);
        Page<AuthenticationDocument> authenticationDocumentPage2 = authenticationRepository.findByParticipantIdAndMissionId(participantId[1], missionId, pageable1);
        Page<AuthenticationDocument> authenticationDocumentPage3 = authenticationRepository.findByParticipantIdAndMissionId(participantId[0], missionId, pageable2);
        Page<AuthenticationDocument> authenticationDocumentPage4 = authenticationRepository.findByParticipantIdAndMissionId(participantId[1], missionId, pageable2);

        List<AuthenticationDocument> authenticationDocumentList1 = authenticationDocumentPage1.getContent();
        List<AuthenticationDocument> authenticationDocumentList2 = authenticationDocumentPage2.getContent();
        List<AuthenticationDocument> authenticationDocumentList3 = authenticationDocumentPage3.getContent();
        List<AuthenticationDocument> authenticationDocumentList4 = authenticationDocumentPage4.getContent();

        Assertions.assertThat(authenticationDocumentList1.size()).isEqualTo(3);
        Assertions.assertThat(authenticationDocumentList1.get(0).getTextData()).isEqualTo(textData[4]);
        Assertions.assertThat(authenticationDocumentList1.get(1).getTextData()).isEqualTo(textData[2]);
        Assertions.assertThat(authenticationDocumentList1.get(2).getTextData()).isEqualTo(textData[0]);

        Assertions.assertThat(authenticationDocumentList2.size()).isEqualTo(3);
        Assertions.assertThat(authenticationDocumentList2.get(0).getTextData()).isEqualTo(textData[5]);
        Assertions.assertThat(authenticationDocumentList2.get(1).getTextData()).isEqualTo(textData[3]);
        Assertions.assertThat(authenticationDocumentList2.get(2).getTextData()).isEqualTo(textData[1]);

        Assertions.assertThat(authenticationDocumentList3.size()).isEqualTo(1);
        Assertions.assertThat(authenticationDocumentList3.get(0).getTextData()).isEqualTo(textData[0]);

        Assertions.assertThat(authenticationDocumentList4.size()).isEqualTo(1);
        Assertions.assertThat(authenticationDocumentList4.get(0).getTextData()).isEqualTo(textData[1]);
    }

    @Test
    @DisplayName("AuthenticationRepository의 findByParticipantIdAndMissionIdAndDateRange 매서드 테스트")
    void findByParticipantIdAndMissionIdAndDateRange() {
        String[] textData = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        ObjectId missionId = new ObjectId("55ea0c8007b2c737d6227bf0");
        ObjectId[] participantId = {new ObjectId("45ea0c8007b2c737d6227bf0"), new ObjectId("45ea0c8007b2c737d6227bf2")};
        LocalDateTime startDate = LocalDateTime.of(2024, 4, 12, 11, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 4, 15, 11, 20);

        List<AuthenticationDocument> authenticationDocumentList1 = authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(participantId[0], missionId, startDate, endDate);
        List<AuthenticationDocument> authenticationDocumentList2 = authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(participantId[1], missionId, startDate, endDate);

        Assertions.assertThat(authenticationDocumentList1.size()).isEqualTo(2);
        Assertions.assertThat(authenticationDocumentList1.get(0).getTextData()).isEqualTo(textData[0]);
        Assertions.assertThat(authenticationDocumentList1.get(1).getTextData()).isEqualTo(textData[2]);

        Assertions.assertThat(authenticationDocumentList2.size()).isEqualTo(2);
        Assertions.assertThat(authenticationDocumentList2.get(0).getTextData()).isEqualTo(textData[1]);
        Assertions.assertThat(authenticationDocumentList2.get(1).getTextData()).isEqualTo(textData[3]);
    }

    @Test
    @DisplayName("AuthenticationRepository의 findByParticipantIdAndMissionIdAndDate 매서드 테스트")
    void findByParticipantIdAndMissionIdAndDate() {
        String[] textData = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        ObjectId missionId = new ObjectId("55ea0c8007b2c737d6227bf0");
        ObjectId[] participantId = {new ObjectId("45ea0c8007b2c737d6227bf0"), new ObjectId("45ea0c8007b2c737d6227bf2")};
        LocalDateTime startOfDay = LocalDateTime.of(2024, 4, 12, 0, 0);
        LocalDateTime endOfDay = LocalDateTime.of(2024, 4, 12, 23, 59);

        Optional<AuthenticationDocument> authenticationDocumentOptional1 = authenticationRepository.findByParticipantIdAndMissionIdAndDate(participantId[0], missionId, startOfDay, endOfDay);
        Optional<AuthenticationDocument> authenticationDocumentOptional2 = authenticationRepository.findByParticipantIdAndMissionIdAndDate(participantId[1], missionId, startOfDay, endOfDay);

        AuthenticationDocument authenticationDocument1 = authenticationDocumentOptional1.get();

        Assertions.assertThat(authenticationDocument1).isNotNull();
        Assertions.assertThat(authenticationDocument1.getTextData()).isEqualTo(textData[0]);

        Assertions.assertThat(authenticationDocumentOptional2).isEmpty();
    }
}
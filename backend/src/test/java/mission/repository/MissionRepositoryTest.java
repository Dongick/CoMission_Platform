package mission.repository;

import mission.document.MissionDocument;
import mission.dto.mission.MissionInfo;
import mission.dto.mission.SimpleMissionInfo;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataMongoTest
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    void setUp() {
        missionRepository.deleteAll();

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4"),
                new ObjectId("65ea0c8007b2c737d6227bf6"), new ObjectId("65ea0c8007b2c737d6227bf8"), new ObjectId("65ea0c8007b2c737d6227bfa")};
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        String[] status = {"CREATED", "STARTED", "COMPLETED", "CREATED", "STARTED", "COMPLETED"};
        int[] participants = {7, 3, 5, 3, 1, 9};

        for (int i = 0; i < ids.length; i++) {
            MissionDocument missionDocument = MissionDocument.builder()
                    .id(ids[i])
                    .createdAt(LocalDateTime.now())
                    .title(title[i])
                    .participants(participants[i])
                    .status(status[i])
                    .build();
            missionRepository.save(missionDocument);
        }
    }

    @Test
    @DisplayName("MissionRepository의 findAllByStatusNotCompletedOrderByCreatedAtDesc 매서드 테스트")
    void findAllByStatusNotCompletedOrderByCreatedAtDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 2);

        //when
        List<MissionInfo> missions1 = missionRepository.findAllByStatusNotCompletedOrderByCreatedAtDesc(pageable1);
        List<MissionInfo> missions2 = missionRepository.findAllByStatusNotCompletedOrderByCreatedAtDesc(pageable2);

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(4);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo(title[4]);
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo(title[3]);
        Assertions.assertThat(missions1.get(2).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missions1.get(3).getTitle()).isEqualTo(title[0]);

        Assertions.assertThat(missions2.size()).isEqualTo(2);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo(title[4]);
        Assertions.assertThat(missions2.get(1).getTitle()).isEqualTo(title[3]);
    }

    @Test
    @DisplayName("MissionRepository의 findAllByStatusNotCompletedOrderByParticipantsDesc 매서드 테스트")
    void findAllByStatusNotCompletedOrderByParticipantsDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 2);

        //when
        List<MissionInfo> missions1 = missionRepository.findAllByStatusNotCompletedOrderByParticipantsDesc(pageable1);
        List<MissionInfo> missions2 = missionRepository.findAllByStatusNotCompletedOrderByParticipantsDesc(pageable2);

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(4);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo(title[0]);
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo(title[3]);
        Assertions.assertThat(missions1.get(2).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missions1.get(3).getTitle()).isEqualTo(title[4]);

        Assertions.assertThat(missions2.size()).isEqualTo(2);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo(title[0]);
        Assertions.assertThat(missions2.get(1).getTitle()).isEqualTo(title[3]);
    }

    @Test
    @DisplayName("MissionRepository의 findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc 매서드 테스트")
    void findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 2);

        //when
        List<MissionInfo> missions1 = missionRepository.findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc(pageable1);
        List<MissionInfo> missions2 = missionRepository.findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc(pageable2);

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(2);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo(title[3]);
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo(title[0]);

        Assertions.assertThat(missions2.size()).isEqualTo(2);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo(title[3]);
        Assertions.assertThat(missions2.get(1).getTitle()).isEqualTo(title[0]);
    }

    @Test
    @DisplayName("MissionRepository의 findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc 매서드 테스트")
    void findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 2);

        //when
        List<MissionInfo> missions1 = missionRepository.findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc(pageable1);
        List<MissionInfo> missions2 = missionRepository.findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc(pageable2);

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(2);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo(title[0]);
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo(title[3]);

        Assertions.assertThat(missions2.size()).isEqualTo(2);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo(title[0]);
        Assertions.assertThat(missions2.get(1).getTitle()).isEqualTo(title[3]);
    }

    @Test
    @DisplayName("MissionRepository의 findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc 매서드 테스트")
    void findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 2);

        //when
        List<MissionInfo> missions1 = missionRepository.findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc(pageable1);
        List<MissionInfo> missions2 = missionRepository.findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc(pageable2);

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(2);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo(title[4]);
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo(title[1]);

        Assertions.assertThat(missions2.size()).isEqualTo(2);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo(title[4]);
        Assertions.assertThat(missions2.get(1).getTitle()).isEqualTo(title[1]);
    }

    @Test
    @DisplayName("MissionRepository의 findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc 매서드 테스트")
    void findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 2);

        //when
        List<MissionInfo> missions1 = missionRepository.findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc(pageable1);
        List<MissionInfo> missions2 = missionRepository.findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc(pageable2);

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(2);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo(title[4]);

        Assertions.assertThat(missions2.size()).isEqualTo(2);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missions2.get(1).getTitle()).isEqualTo(title[4]);
    }


    @Test
    @DisplayName("MissionRepository의 findByMissionIdInAndStatusNotOrderByCreatedAtDesc 매서드 테스트")
    void findByMissionIdInAndStatusNotOrderByCreatedAtDesc() {

        //given
        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};
        String[] title = {"Mission 1", "Mission 2", "Mission 3"};

        List<ObjectId> objectIdList = new ArrayList<>();
        for(int i = 0; i < ids.length; i++) {
            objectIdList.add(ids[i]);
        }

        //when
        List<MissionInfo> missions = missionRepository.findByMissionIdInAndStatusNotOrderByCreatedAtDesc(objectIdList);

        //then
        Assertions.assertThat(missions.size()).isEqualTo(2);
        Assertions.assertThat(missions.get(0).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missions.get(1).getTitle()).isEqualTo(title[0]);
    }

    @Test
    @DisplayName("MissionRepository의 findById 매서드 테스트")
    void findById() {

        //given
        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};
        String[] title = {"Mission 1", "Mission 2", "Mission 3"};

        //when
        Optional<MissionDocument> missionDocumentOptional1 = missionRepository.findById(ids[0].toString());
        Optional<MissionDocument> missionDocumentOptional2 = missionRepository.findById(ids[1].toString());

        MissionDocument mission1 = missionDocumentOptional1.get();
        MissionDocument mission2 = missionDocumentOptional2.get();

        //then
        Assertions.assertThat(mission1.getId()).isEqualTo(ids[0]);
        Assertions.assertThat(mission1.getTitle()).isEqualTo(title[0]);

        Assertions.assertThat(mission2.getId()).isEqualTo(ids[1]);
        Assertions.assertThat(mission2.getTitle()).isEqualTo(title[1]);
    }

    @Test
    @DisplayName("MissionRepository의 findByStatus 매서드 테스트")
    void findByStatus() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        String[] status = {"CREATED", "STARTED", "COMPLETED"};

        //when
        List<MissionDocument> missionDocumentList1 = missionRepository.findByStatus(status[0]);
        List<MissionDocument> missionDocumentList2 = missionRepository.findByStatus(status[1]);
        List<MissionDocument> missionDocumentList3 = missionRepository.findByStatus(status[2]);

        //then
        Assertions.assertThat(missionDocumentList1.size()).isEqualTo(2);
        Assertions.assertThat(missionDocumentList1.get(0).getStatus()).isEqualTo(status[0]);
        Assertions.assertThat(missionDocumentList1.get(0).getTitle()).isEqualTo(title[0]);
        Assertions.assertThat(missionDocumentList1.get(1).getStatus()).isEqualTo(status[0]);
        Assertions.assertThat(missionDocumentList1.get(1).getTitle()).isEqualTo(title[3]);

        Assertions.assertThat(missionDocumentList2.size()).isEqualTo(2);
        Assertions.assertThat(missionDocumentList2.get(0).getStatus()).isEqualTo(status[1]);
        Assertions.assertThat(missionDocumentList2.get(0).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missionDocumentList2.get(1).getStatus()).isEqualTo(status[1]);
        Assertions.assertThat(missionDocumentList2.get(1).getTitle()).isEqualTo(title[4]);

        Assertions.assertThat(missionDocumentList3.size()).isEqualTo(2);
        Assertions.assertThat(missionDocumentList3.get(0).getStatus()).isEqualTo(status[2]);
        Assertions.assertThat(missionDocumentList3.get(0).getTitle()).isEqualTo(title[2]);
        Assertions.assertThat(missionDocumentList3.get(1).getStatus()).isEqualTo(status[2]);
        Assertions.assertThat(missionDocumentList3.get(1).getTitle()).isEqualTo(title[5]);
    }

    @Test
    @DisplayName("MissionRepository의 findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc 매서드 테스트")
    void findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc() {
        //given
        String[] title = {"Mission 1", "Mission 2", "Mission 3", "Mission 4", "Mission 5", "Mission 6"};
        String[] regexTitle = {"mi", "MIS", "misq", "mission 2", "mission 3"};

        //when
        List<MissionInfo> missionInfoList1 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(regexTitle[0]);
        List<MissionInfo> missionInfoList2 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(regexTitle[1]);
        List<MissionInfo> missionInfoList3 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(regexTitle[2]);
        List<MissionInfo> missionInfoList4 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(regexTitle[3]);
        List<MissionInfo> missionInfoList5 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(regexTitle[4]);

        //then
        Assertions.assertThat(missionInfoList1.size()).isEqualTo(4);
        Assertions.assertThat(missionInfoList1.get(0).getTitle()).isEqualTo(title[4]);
        Assertions.assertThat(missionInfoList1.get(1).getTitle()).isEqualTo(title[3]);
        Assertions.assertThat(missionInfoList1.get(2).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missionInfoList1.get(3).getTitle()).isEqualTo(title[0]);

        Assertions.assertThat(missionInfoList2.size()).isEqualTo(4);
        Assertions.assertThat(missionInfoList2.get(0).getTitle()).isEqualTo(title[4]);
        Assertions.assertThat(missionInfoList2.get(1).getTitle()).isEqualTo(title[3]);
        Assertions.assertThat(missionInfoList2.get(2).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missionInfoList2.get(3).getTitle()).isEqualTo(title[0]);

        Assertions.assertThat(missionInfoList3.size()).isEqualTo(0);

        Assertions.assertThat(missionInfoList4.size()).isEqualTo(1);
        Assertions.assertThat(missionInfoList4.get(0).getTitle()).isEqualTo(title[1]);

        Assertions.assertThat(missionInfoList5.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("MissionRepository의 findByIdInOrderByCreatedAtDesc 매서드 테스트")
    void findByIdInOrderByCreatedAtDesc() {
        //given
        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};
        String[] title = {"Mission 1", "Mission 2", "Mission 3"};

        List<ObjectId> objectIdList = new ArrayList<>();
        for(int i = 0; i < ids.length; i++) {
            objectIdList.add(ids[i]);
        }

        //when
        List<SimpleMissionInfo> missionInfoList = missionRepository.findByIdInOrderByCreatedAtDesc(objectIdList);

        //then
        Assertions.assertThat(missionInfoList.size()).isEqualTo(3);
        Assertions.assertThat(missionInfoList.get(0).getTitle()).isEqualTo(title[2]);
        Assertions.assertThat(missionInfoList.get(1).getTitle()).isEqualTo(title[1]);
        Assertions.assertThat(missionInfoList.get(2).getTitle()).isEqualTo(title[0]);

    }
}
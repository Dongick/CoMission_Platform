package mission.repository;

import mission.document.MissionDocument;
import mission.dto.mission.MissionInfo;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
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
    void tearDown() {
        missionRepository.deleteAll(); // 테스트가 시작 전 모든 데이터 삭제
    }

    @Test
    void findAllAndStatusNotByOrderByCreatedAtDesc() {
        //given
        MissionDocument missionDocument1 = MissionDocument.builder()
                .createdAt(LocalDateTime.now())
                .title("Mission 1")
                .minParticipants(3)
                .participants(1)
                .duration(10)
                .status("CREATED")
                .frequency("매일")
                .build();
        missionRepository.save(missionDocument1);

        MissionDocument missionDocument2 = MissionDocument.builder()
                .createdAt(LocalDateTime.now().plusDays(1))
                .title("Mission 2")
                .minParticipants(1)
                .participants(3)
                .duration(15)
                .status("STARTED")
                .frequency("주1회")
                .build();
        missionRepository.save(missionDocument2);

        MissionDocument missionDocument3 = MissionDocument.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .title("Mission 3")
                .minParticipants(2)
                .participants(5)
                .duration(12)
                .status("COMPLETED")
                .frequency("주2회")
                .build();
        missionRepository.save(missionDocument3);

        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(0, 1);

        //when
        Page<MissionInfo> missions1Page = missionRepository.findAllAndStatusNotByOrderByCreatedAtDesc(pageable1);
        Page<MissionInfo> missions2Page = missionRepository.findAllAndStatusNotByOrderByCreatedAtDesc(pageable2);

        List<MissionInfo> missions1 = missions1Page.getContent();
        List<MissionInfo> missions2 = missions2Page.getContent();

        //then
        Assertions.assertThat(missions1.size()).isEqualTo(2);
        Assertions.assertThat(missions1.get(0).getTitle()).isEqualTo("Mission 2");
        Assertions.assertThat(missions1.get(1).getTitle()).isEqualTo("Mission 1");

        Assertions.assertThat(missions2.size()).isEqualTo(1);
        Assertions.assertThat(missions2.get(0).getTitle()).isEqualTo("Mission 2");
    }

    @Test
    void findByMissionIdInAndStatusNotOrderByCreatedAtDesc() {

        //given
        ObjectId id1 = new ObjectId("65ea0c8007b2c737d6227bf0");
        ObjectId id2 = new ObjectId("65ea0c8007b2c737d6227bf2");
        ObjectId id3 = new ObjectId("65ea0c8007b2c737d6227bf4");

        MissionDocument missionDocument1 = MissionDocument.builder()
                .id(id1)
                .createdAt(LocalDateTime.now())
                .title("Mission 1")
                .minParticipants(3)
                .participants(1)
                .duration(10)
                .status("CREATED")
                .frequency("매일")
                .build();
        missionRepository.save(missionDocument1);

        MissionDocument missionDocument2 = MissionDocument.builder()
                .id(id2)
                .createdAt(LocalDateTime.now().plusDays(1))
                .title("Mission 2")
                .minParticipants(1)
                .participants(3)
                .duration(15)
                .status("STARTED")
                .frequency("주1회")
                .build();
        missionRepository.save(missionDocument2);

        MissionDocument missionDocument3 = MissionDocument.builder()
                .id(id3)
                .createdAt(LocalDateTime.now().minusDays(1))
                .title("Mission 3")
                .minParticipants(2)
                .participants(5)
                .duration(12)
                .status("COMPLETED")
                .frequency("주2회")
                .build();
        missionRepository.save(missionDocument3);

        List<ObjectId> objectIdList = new ArrayList<>();
        objectIdList.add(id1);
        objectIdList.add(id2);
        objectIdList.add(id3);

        //when
        List<MissionInfo> missions = missionRepository.findByMissionIdInAndStatusNotOrderByCreatedAtDesc(objectIdList);

        //then
        Assertions.assertThat(missions.size()).isEqualTo(2);
        Assertions.assertThat(missions.get(0).getTitle()).isEqualTo("Mission 2");
        Assertions.assertThat(missions.get(1).getTitle()).isEqualTo("Mission 1");
    }

    @Test
    void findById() {

        //given
        ObjectId id1 = new ObjectId("65ea0c8007b2c737d6227bf0");
        ObjectId id2 = new ObjectId("65ea0c8007b2c737d6227bf2");
        ObjectId id3 = new ObjectId("65ea0c8007b2c737d6227bf4");

        MissionDocument missionDocument1 = MissionDocument.builder()
                .id(id1)
                .title("Mission 1")
                .build();
        missionRepository.save(missionDocument1);

        MissionDocument missionDocument2 = MissionDocument.builder()
                .id(id2)
                .title("Mission 2")
                .build();
        missionRepository.save(missionDocument2);

        MissionDocument missionDocument3 = MissionDocument.builder()
                .id(id3)
                .title("Mission 3")
                .build();
        missionRepository.save(missionDocument3);

        //when
        Optional<MissionDocument> missionDocumentOptional1 = missionRepository.findById(id1.toString());
        Optional<MissionDocument> missionDocumentOptional2 = missionRepository.findById(id2.toString());

        MissionDocument mission1 = missionDocumentOptional1.get();
        MissionDocument mission2 = missionDocumentOptional2.get();

        //then
        Assertions.assertThat(mission1.getId()).isEqualTo(id1);
        Assertions.assertThat(mission1.getTitle()).isEqualTo("Mission 1");

        Assertions.assertThat(mission2.getId()).isEqualTo(id2);
        Assertions.assertThat(mission2.getTitle()).isEqualTo("Mission 2");
    }

    @Test
    void findByStatus() {
        //given
        String status1 = "CREATED";
        String status2 = "STARTED";
        String status3 = "COMPLETED";

        MissionDocument missionDocument1 = MissionDocument.builder()
                .title("Mission 1")
                .status(status1)
                .build();
        missionRepository.save(missionDocument1);

        MissionDocument missionDocument2 = MissionDocument.builder()
                .title("Mission 2")
                .status(status1)
                .build();
        missionRepository.save(missionDocument2);

        MissionDocument missionDocument3 = MissionDocument.builder()
                .title("Mission 3")
                .status(status2)
                .build();
        missionRepository.save(missionDocument3);

        MissionDocument missionDocument4 = MissionDocument.builder()
                .title("Mission 4")
                .status(status2)
                .build();
        missionRepository.save(missionDocument4);

        MissionDocument missionDocument5 = MissionDocument.builder()
                .title("Mission 5")
                .status(status3)
                .build();
        missionRepository.save(missionDocument5);

        MissionDocument missionDocument6 = MissionDocument.builder()
                .title("Mission 6")
                .status(status3)
                .build();
        missionRepository.save(missionDocument6);

        //when
        List<MissionDocument> missionDocumentList1 = missionRepository.findByStatus(status1);
        List<MissionDocument> missionDocumentList2 = missionRepository.findByStatus(status2);
        List<MissionDocument> missionDocumentList3 = missionRepository.findByStatus(status3);

        //then
        Assertions.assertThat(missionDocumentList1.size()).isEqualTo(2);
        Assertions.assertThat(missionDocumentList1.get(0).getStatus()).isEqualTo(status1);
        Assertions.assertThat(missionDocumentList1.get(0).getTitle()).isEqualTo("Mission 1");
        Assertions.assertThat(missionDocumentList1.get(1).getStatus()).isEqualTo(status1);
        Assertions.assertThat(missionDocumentList1.get(1).getTitle()).isEqualTo("Mission 2");

        Assertions.assertThat(missionDocumentList2.size()).isEqualTo(2);
        Assertions.assertThat(missionDocumentList2.get(0).getStatus()).isEqualTo(status2);
        Assertions.assertThat(missionDocumentList2.get(0).getTitle()).isEqualTo("Mission 3");
        Assertions.assertThat(missionDocumentList2.get(1).getStatus()).isEqualTo(status2);
        Assertions.assertThat(missionDocumentList2.get(1).getTitle()).isEqualTo("Mission 4");

        Assertions.assertThat(missionDocumentList3.size()).isEqualTo(2);
        Assertions.assertThat(missionDocumentList3.get(0).getStatus()).isEqualTo(status3);
        Assertions.assertThat(missionDocumentList3.get(0).getTitle()).isEqualTo("Mission 5");
        Assertions.assertThat(missionDocumentList3.get(1).getStatus()).isEqualTo(status3);
        Assertions.assertThat(missionDocumentList3.get(1).getTitle()).isEqualTo("Mission 6");
    }

    @Test
    void findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc() {
        //given
        MissionDocument missionDocument1 = MissionDocument.builder()
                .createdAt(LocalDateTime.now())
                .title("Mission 1")
                .status("CREATED")
                .build();
        missionRepository.save(missionDocument1);

        MissionDocument missionDocument2 = MissionDocument.builder()
                .createdAt(LocalDateTime.now().plusDays(1))
                .title("Mission 2")
                .status("STARTED")
                .build();
        missionRepository.save(missionDocument2);

        MissionDocument missionDocument3 = MissionDocument.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .title("Mission 3")
                .status("COMPLETED")
                .build();
        missionRepository.save(missionDocument3);

        String title1 = "mi";
        String title2 = "MIS";
        String title3 = "misq";
        String title4 = "mission 2";
        String title5 = "mission 3";

        //when
        List<MissionInfo> missionInfoList1 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(title1);
        List<MissionInfo> missionInfoList2 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(title2);
        List<MissionInfo> missionInfoList3 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(title3);
        List<MissionInfo> missionInfoList4 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(title4);
        List<MissionInfo> missionInfoList5 = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(title5);

        //then
        Assertions.assertThat(missionInfoList1.size()).isEqualTo(2);
        Assertions.assertThat(missionInfoList1.get(0).getTitle()).isEqualTo("Mission 2");
        Assertions.assertThat(missionInfoList1.get(1).getTitle()).isEqualTo("Mission 1");

        Assertions.assertThat(missionInfoList2.size()).isEqualTo(2);
        Assertions.assertThat(missionInfoList2.get(0).getTitle()).isEqualTo("Mission 2");
        Assertions.assertThat(missionInfoList2.get(1).getTitle()).isEqualTo("Mission 1");

        Assertions.assertThat(missionInfoList3.size()).isEqualTo(0);

        Assertions.assertThat(missionInfoList4.size()).isEqualTo(1);
        Assertions.assertThat(missionInfoList2.get(0).getTitle()).isEqualTo("Mission 2");

        Assertions.assertThat(missionInfoList5.size()).isEqualTo(0);
    }
}
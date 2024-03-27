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

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataMongoTest
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    void tearDown() {
        missionRepository.deleteAll(); // 테스트가 끝난 후 모든 데이터 삭제
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

        ObjectId id1 = new ObjectId("65ea0c8007b2c737d6227bf0");
        ObjectId id2 = new ObjectId("65ea0c8007b2c737d6227bf2");
        ObjectId id3 = new ObjectId("65ea0c8007b2c737d6227bf4");

        //given
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
    void findByTitle() {
    }

    @Test
    void findByStatus() {
    }

    @Test
    void findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc() {
    }
}
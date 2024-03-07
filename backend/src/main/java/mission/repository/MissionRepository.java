package mission.repository;

import mission.document.MissionDocument;
import mission.dto.mission.MissionInfo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MissionRepository extends MongoRepository<MissionDocument, ObjectId> {
    @Query(value= "{status: {$ne: 'COMPLETED'}}", fields="{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query(value= "{status: {$ne: 'COMPLETED'}}", fields="{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findByMissionIdInOrderByCreatedAtAsc(List<ObjectId> missionIdList);

    MissionDocument findByTitle(String title);

    List<MissionDocument> findByStatus(String status);
}

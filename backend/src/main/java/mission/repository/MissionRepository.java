package mission.repository;

import mission.document.MissionDocument;
import mission.dto.mission.MissionInfo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends MongoRepository<MissionDocument, ObjectId> {
    @Query(value= "{status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}", fields="{id:1, username:1, creatorEmail:1, title:1, photoUrl:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findAllByStatusNotOrderByCreatedAtDesc(Pageable pageable);
    @Query(value= "{_id: {$in: ?0}, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}", fields="{id:1, username:1, creatorEmail:1, title:1, photoUrl:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findByMissionIdInAndStatusNotOrderByCreatedAtDesc(List<ObjectId> missionIdList);

    Optional<MissionDocument> findById(String Id);

    List<MissionDocument> findByStatus(String status);

    @Query(value = "{title: {$regex: ?0, $options: 'i'}, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}", fields = "{id:1, username:1, creatorEmail:1, title:1, photoUrl:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(String regex);
}

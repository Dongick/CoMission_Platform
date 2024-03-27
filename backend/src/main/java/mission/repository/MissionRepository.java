package mission.repository;

import mission.document.MissionDocument;
import mission.dto.mission.MissionInfo;
import org.bson.types.ObjectId;
<<<<<<< HEAD
=======
import org.springframework.data.domain.Page;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends MongoRepository<MissionDocument, ObjectId> {
<<<<<<< HEAD
    @Query(value= "{status: {$ne: 'COMPLETED'}}", fields="{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query(value= "{_id: {$in: ?0}, status: {$ne: 'COMPLETED'}}", fields="{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findByMissionIdInOrderByCreatedAtAsc(List<ObjectId> missionIdList);
=======
    @Query(value= "{status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}", fields="{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    Page<MissionInfo> findAllAndStatusNotByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value= "{_id: {$in: ?0}, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}", fields="{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findByMissionIdInAndStatusNotOrderByCreatedAtDesc(List<ObjectId> missionIdList);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

    Optional<MissionDocument> findByTitle(String title);

    List<MissionDocument> findByStatus(String status);
<<<<<<< HEAD
=======

    @Query(value = "{title: {$regex: ?0, $options: 'i'}, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}", fields = "{title:1, minParticipants:1, participants:1, duration:1, status:1, frequency:1}")
    List<MissionInfo> findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(String regex);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
}

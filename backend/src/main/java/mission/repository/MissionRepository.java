package mission.repository;

import mission.document.MissionDocument;
import mission.dto.mission.MissionInfo;
import mission.dto.mission.SimpleMissionInfo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends MongoRepository<MissionDocument, ObjectId> {
    @Query(value= "{status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}")
    List<MissionInfo> findAllByStatusNotCompletedOrderByCreatedAtDesc(Pageable pageable);

    @Query(value= "{status: {$ne: 'COMPLETED'}}", sort = "{participants: -1, createdAt: -1}")
    List<MissionInfo> findAllByStatusNotCompletedOrderByParticipantsDesc(Pageable pageable);

    @Query(value= "{status: {$nin: ['COMPLETED', 'STARTED']}}", sort = "{createdAt: -1}")
    List<MissionInfo> findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc(Pageable pageable);

    @Query(value= "{status: {$nin: ['COMPLETED', 'STARTED']}}", sort = "{participants: -1, createdAt: -1}")
    List<MissionInfo> findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc(Pageable pageable);

    @Query(value= "{status: {$nin: ['COMPLETED', 'CREATED']}}", sort = "{createdAt: -1}")
    List<MissionInfo> findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc(Pageable pageable);

    @Query(value= "{status: {$nin: ['COMPLETED', 'CREATED']}}", sort = "{participants: -1, createdAt: -1}")
    List<MissionInfo> findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc(Pageable pageable);

    @Query(value= "{_id: {$in: ?0}, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}")
    List<MissionInfo> findByMissionIdInAndStatusNotOrderByCreatedAtDesc(List<ObjectId> missionIdList);

    Optional<MissionDocument> findById(String Id);

    List<MissionDocument> findByStatus(String status);

    @Query(value = "{title: {$regex: ?0, $options: 'i'}, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}")
    List<MissionInfo> findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(String regex);

    @Query(value= "{_id: {$in: ?0}}", sort = "{createdAt: -1}")
    List<SimpleMissionInfo> findByIdInOrderByCreatedAtDesc(List<ObjectId> missionIdList);
}

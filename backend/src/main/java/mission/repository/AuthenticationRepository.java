package mission.repository;

import mission.document.AuthenticationDocument;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuthenticationRepository extends MongoRepository<AuthenticationDocument, ObjectId> {

    Page<AuthenticationDocument> findByMissionId(ObjectId missionId, Pageable pageable);

    Page<AuthenticationDocument> findByParticipantIdAndMissionId(ObjectId participantId, ObjectId missionId, Pageable pageable);

    @Query(value = "{ 'participantId': ?0, 'missionId': ?1, 'date': { $gte: ?2, $lte: ?3 } }", sort = "{date: 1}")
    List<AuthenticationDocument> findByParticipantIdAndMissionIdAndDateRange(ObjectId participantId, ObjectId missionId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'participantId': ?0, 'missionId': ?1, 'date': { $gte: ?2, $lte: ?3 } }")
    Optional<AuthenticationDocument> findByParticipantIdAndMissionIdAndDate(ObjectId participantId, ObjectId missionId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}

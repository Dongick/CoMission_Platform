package mission.repository;

import mission.document.ParticipantDocument;
import mission.dto.participant.ParticipantMissionId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends MongoRepository<ParticipantDocument, ObjectId> {
    @Query(value = "{userEmail: ?0, status: {$ne: 'COMPLETED'}}", fields="{missionId:1}")
<<<<<<< HEAD
    List<ParticipantMissionId> findByUserEmailAndStatsNot(String email);
=======
    List<ParticipantMissionId> findByUserEmailAndStatusNot(String email);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

    List<ParticipantDocument> findByMissionId(ObjectId missionId);

    Optional<ParticipantDocument> findByMissionIdAndUserEmail(ObjectId missionId, String userEmail);
}

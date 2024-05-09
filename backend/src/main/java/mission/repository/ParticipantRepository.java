package mission.repository;

import mission.document.ParticipantDocument;
import mission.dto.participant.ParticipantMissionId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends MongoRepository<ParticipantDocument, ObjectId> {

    List<ParticipantDocument> findByUserEmail(String email);

    List<ParticipantDocument> findByMissionId(ObjectId missionId);

    Optional<ParticipantDocument> findByMissionIdAndUserEmail(ObjectId missionId, String userEmail);
}

package mission.repository;

import mission.document.ParticipantDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends MongoRepository<ParticipantDocument, ObjectId> {
    @Query(value = "{userEmail: ?0}", fields="{missionId:1}")
    List<ParticipantMissionId> findByUserEmail(String email);

    List<ParticipantDocument> findByMissionId(ObjectId missionId);

    Optional<ParticipantDocument> findByMissionIdAndUserEmail(ObjectId missionId, String userEmail);
}

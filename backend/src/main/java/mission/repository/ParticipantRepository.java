package mission.repository;

import mission.document.ParticipantDocument;
import mission.dto.participant.ParticipantMissionId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ParticipantRepository extends MongoRepository<ParticipantDocument, ObjectId> {
    @Query(value = "{status: {$ne: 'COMPLETED'}}", fields="{missionId:1}")
    List<ParticipantMissionId> findByEmail(String email);

    List<ParticipantDocument> findByMissionId(ObjectId missionId);

    ParticipantDocument findByMissionIdAndUserEmail(ObjectId missionId, String userEmail);
}

package mission.dto.participant;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ParticipantMissionId {
    private ObjectId missionId;
}

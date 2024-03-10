package mission.dto.mission;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionUpdateRequest {
    private String beforeTitle;
    private String afterTitle;
    private String description;
    private int minParticipants;
    private int duration;
    private String frequency;
}

package mission.dto.mission;

import lombok.Data;
@Data
public class MissionCreateRequest {
    private String title;
    private String description;
    private int minParticipants;
    private int duration;
    private String frequency;
    private String creatorEmail;
}

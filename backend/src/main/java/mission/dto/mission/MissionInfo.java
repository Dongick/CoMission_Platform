package mission.dto.mission;

import lombok.Data;

@Data
public class MissionInfo {
    private String id;
    private String title;
    private int minParticipants;
    private int participants;
    private int duration;
    private String status;
    private String frequency;
}

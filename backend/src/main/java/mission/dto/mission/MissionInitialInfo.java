package mission.dto.mission;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MissionInitialInfo {
    private String title;
    private int minParticipants;
    private int participants;
    private int duration;
    private String status;
    private String frequency;
}

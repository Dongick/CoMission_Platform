package mission.dto.mission;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MissionInfoResponse {
    private String title;
    private String description;
    private String status;
    private int minParticipants;
    private int participants;
    private int duration;
    private String frequency;
    private LocalDate deadline;
    private Boolean participant;
}

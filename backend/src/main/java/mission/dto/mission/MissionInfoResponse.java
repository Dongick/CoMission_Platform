package mission.dto.mission;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MissionInfoResponse {
    private String id;
    private String title;
    private String description;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private LocalDate deadline;
    private int minParticipants;
    private int participants;
    private int duration;
    private String status;
    private String frequency;
    private String creatorEmail;
    private Boolean participant;
}

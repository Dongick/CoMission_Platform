package mission.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserMissionPost {
    private LocalDateTime date;
    private String photoData;
    private String textData;
}
package mission.dto.mission;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MissionSearchRequest {
    @NotBlank
    private String title;
}

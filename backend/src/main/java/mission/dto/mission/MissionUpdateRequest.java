package mission.dto.mission;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MissionUpdateRequest {
    @NotBlank
    private String afterTitle;
    @NotBlank
    private String description;
    @Min(1)
    private int minParticipants;
    @Min(1)
    private int duration;
    @NotBlank
    private String frequency;
}

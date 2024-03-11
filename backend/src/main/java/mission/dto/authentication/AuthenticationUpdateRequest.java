package mission.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationUpdateRequest {
    private String photoData;
    @NotBlank
    private String textData;
}

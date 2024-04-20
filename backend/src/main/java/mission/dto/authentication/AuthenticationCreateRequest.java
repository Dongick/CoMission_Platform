package mission.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationCreateRequest {
    @NotBlank
    private String textData;
}

package mission.dto.authentication;

import lombok.Data;

@Data
public class AuthenticationUpdateRequest {
    private String title;
    private String photoData;
    private String textData;
}

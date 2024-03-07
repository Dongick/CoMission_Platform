package mission.dto.authentication;

import lombok.Data;

@Data
public class AuthenticationCreateRequest {
    private String title;
    private String photoData;
    private String textData;
}

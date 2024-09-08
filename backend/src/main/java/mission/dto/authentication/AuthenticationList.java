package mission.dto.authentication;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthenticationList {
    private String username;
    private String userEmail;
    private LocalDateTime date;
    private boolean completed;
    private String photoData;
    private String textData;
}

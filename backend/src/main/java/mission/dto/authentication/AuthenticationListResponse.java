package mission.dto.authentication;

import lombok.Builder;
import lombok.Data;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import java.util.Map;
import java.util.List;

@Data
@Builder
public class AuthenticationListResponse {
<<<<<<< HEAD
    private Map<LocalDate, List<Map<String, Object>>> authenticationData;

    public AuthenticationListResponse(Map<LocalDate, List<Map<String, Object>>> authenticationData) {
=======
    private List<Map<String, Object>> authenticationData;

    public AuthenticationListResponse(List<Map<String, Object>> authenticationData) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
        this.authenticationData = authenticationData;
    }
}

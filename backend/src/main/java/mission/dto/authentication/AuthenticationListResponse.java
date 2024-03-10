package mission.dto.authentication;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class AuthenticationListResponse {
    private Map<LocalDate, List<Map<String, Object>>> authenticationData;

    public AuthenticationListResponse(Map<LocalDate, List<Map<String, Object>>> authenticationData) {
        this.authenticationData = authenticationData;
    }
}

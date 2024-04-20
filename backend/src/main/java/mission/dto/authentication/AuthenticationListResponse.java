package mission.dto.authentication;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.List;

@Data
@Builder
public class AuthenticationListResponse {
    private List<Map<String, Object>> authenticationData;

    public AuthenticationListResponse(List<Map<String, Object>> authenticationData) {
        this.authenticationData = authenticationData;
    }
}

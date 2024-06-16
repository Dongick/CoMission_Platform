package mission.dto.authentication;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthenticationListResponse {
    private List<AuthenticationList> authenticationData;

    public AuthenticationListResponse(List<AuthenticationList> authenticationData) {
        this.authenticationData = authenticationData;
    }
}

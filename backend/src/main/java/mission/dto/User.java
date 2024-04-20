package mission.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private String role;
    private String name;
    private String email;
}

package mission.dto.user;

import lombok.Data;
import mission.dto.mission.SimpleMissionInfo;

import java.util.List;

@Data
public class UserPostResponse {
    private final List<SimpleMissionInfo> simpleMissionInfoList;

    public UserPostResponse(List<SimpleMissionInfo> simpleMissionInfoList) {
        this.simpleMissionInfoList = simpleMissionInfoList;
    }
}
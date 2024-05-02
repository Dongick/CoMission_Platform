package mission.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserMissionPostResponse {
    private final List<UserMissionPost> userMissionPostList;

    public UserMissionPostResponse(List<UserMissionPost> userMissionPostList) {
        this.userMissionPostList = userMissionPostList;
    }
}

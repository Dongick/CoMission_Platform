package mission.dto.mission;

import lombok.Data;

import java.util.List;

@Data
public class MissionSearchResponse {
    private final List<MissionInfo> missionInfoList;

    public MissionSearchResponse(List<MissionInfo> missionInfoList) {
        this.missionInfoList = missionInfoList;
    }
}

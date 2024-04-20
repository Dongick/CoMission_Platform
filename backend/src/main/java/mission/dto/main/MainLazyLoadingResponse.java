package mission.dto.main;

import lombok.Data;
import mission.dto.mission.MissionInfo;

import java.util.List;

@Data
public class MainLazyLoadingResponse {
    private final List<MissionInfo> missionInfoList;

    public MainLazyLoadingResponse(List<MissionInfo> missionInfoList) {
        this.missionInfoList = missionInfoList;
    }
}

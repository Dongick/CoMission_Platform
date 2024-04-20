package mission.dto.main;

import lombok.Data;
import mission.dto.mission.MissionInfo;

import java.util.List;

@Data
public class MainResponse {
    private final List<MissionInfo> participantMissionInfoList;
    private final List<MissionInfo> missionInfoList;

    public MainResponse(List<MissionInfo> participantMissionInfoList, List<MissionInfo> missionInfoList) {
        this.participantMissionInfoList = participantMissionInfoList;
        this.missionInfoList = missionInfoList;
    }
}

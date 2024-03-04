package mission.dto.main;

import lombok.Data;
import mission.dto.mission.MissionInitialInfo;

import java.util.List;

@Data
public class MainInitialResponse {
    private final List<MissionInitialInfo> participantMissionInfoList;
    private final List<MissionInitialInfo> missionInitialInfoList;

    public MainInitialResponse(List<MissionInitialInfo> participantMissionInfoList, List<MissionInitialInfo> missionInitialInfoList) {
        this.participantMissionInfoList = participantMissionInfoList;
        this.missionInitialInfoList = missionInitialInfoList;
    }
}

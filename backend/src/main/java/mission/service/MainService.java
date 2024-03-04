package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.main.MainInitialResponse;
import mission.dto.mission.MissionInitialInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantMissionId;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;

    public MainInitialResponse getInitialMissionList() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<MissionInitialInfo> participantMissionInfoList = null;

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            List<ParticipantMissionId> participantMissionIdList = participantRepository.findByEmail(userEmail);

            List<ObjectId> missionIdList = participantMissionIdList.stream()
                    .map(ParticipantMissionId::getMissionId)
                    .collect(Collectors.toList());

            if(!missionIdList.isEmpty()) {
                participantMissionInfoList = missionRepository.findByMissionIdInOrderByCreatedAtAsc(missionIdList);
            }
        }

        Pageable pageable = PageRequest.of(0, 20);

        List<MissionInitialInfo> missionInitialInfoList = missionRepository.findAllByOrderByCreatedAtAsc(pageable);

        MainInitialResponse mainInitialResponse = new MainInitialResponse(participantMissionInfoList, missionInitialInfoList);

        return mainInitialResponse;
    }
}

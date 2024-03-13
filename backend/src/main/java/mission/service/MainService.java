package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.main.MainResponse;
import mission.dto.mission.MissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantMissionId;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;

    // 미션 목록을 보여주는 매서드
    @Transactional
    public MainResponse getInitialMissionList() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        List<MissionInfo> participantMissionInfoList = null;

        List<ParticipantMissionId> participantMissionIdList = participantRepository.findByUserEmailAndStatsNot(userEmail);

        List<ObjectId> missionIdList = participantMissionIdList.stream()
                .map(ParticipantMissionId::getMissionId)
                .collect(Collectors.toList());

        // 로그인을 진행한 사용자이면 현재 참가한 미션 목록
        if(!missionIdList.isEmpty()) {
            participantMissionInfoList = missionRepository.findByMissionIdInOrderByCreatedAtAsc(missionIdList);
        }

        Pageable pageable = PageRequest.of(0, 20);

        // 현재 참여 가능한 미션 목록
        List<MissionInfo> missionInfoList = missionRepository.findAllByOrderByCreatedAtAsc(pageable);

        MainResponse mainResponse = new MainResponse(participantMissionInfoList, missionInfoList);

        return mainResponse;
    }
}

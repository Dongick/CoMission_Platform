package mission.service;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import mission.dto.main.MainLazyLoadingResponse;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import mission.dto.main.MainResponse;
import mission.dto.mission.MissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantMissionId;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
<<<<<<< HEAD
=======
import org.springframework.data.domain.Page;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
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

<<<<<<< HEAD
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
=======
        List<MissionInfo> participantMissionInfoList = null;

        if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            List<ParticipantMissionId> participantMissionIdList = participantRepository.findByUserEmailAndStatusNot(userEmail);

            List<ObjectId> missionIdList = participantMissionIdList.stream()
                    .map(ParticipantMissionId::getMissionId)
                    .collect(Collectors.toList());

            // 로그인을 진행한 사용자이면 현재 참가한 미션 목록
            if(!missionIdList.isEmpty()) {
                participantMissionInfoList = missionRepository.findByMissionIdInAndStatusNotOrderByCreatedAtDesc(missionIdList);
            }
        }

        Page<MissionInfo> missionInfoPage = getMissionList(0);

        List<MissionInfo> missionInfoList = missionInfoPage.getContent();
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        MainResponse mainResponse = new MainResponse(participantMissionInfoList, missionInfoList);

        return mainResponse;
    }
<<<<<<< HEAD
=======

    // 메인화면 lazy loading 시 이후 미션 목록을 보여주는 메서드
    public MainLazyLoadingResponse getLazyLoadingMissionList(int num) {

        Page<MissionInfo> missionInfoPage = getMissionList(num);

        List<MissionInfo> missionInfoList = missionInfoPage.getContent();

        MainLazyLoadingResponse mainLazyLoadingResponse = new MainLazyLoadingResponse(missionInfoList);

        return mainLazyLoadingResponse;
    }

    // 현재 참여 가능한 미션 목록
    private Page<MissionInfo> getMissionList(int num) {
        Pageable pageable = PageRequest.of(20*num, 20*(num+1));

        return missionRepository.findAllAndStatusNotByOrderByCreatedAtDesc(pageable);
    }
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
}

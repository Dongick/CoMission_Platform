package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.main.MainResponse;
import mission.dto.mission.MissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantMissionId;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
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
    public MainResponse getMainMissionList(String sort, int num, String filter) {
        List<MissionInfo> participantMissionInfoList = null;

        if(num == 0) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof CustomOAuth2User) {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
                String userEmail = customOAuth2User.getEmail();

                List<ParticipantMissionId> participantMissionIdList = participantRepository.findByUserEmail(userEmail);

                List<ObjectId> missionIdList = participantMissionIdList.stream()
                        .map(ParticipantMissionId::getMissionId)
                        .collect(Collectors.toList());

                // 로그인을 진행한 사용자이면 현재 참가한 미션 목록
                if(!missionIdList.isEmpty()) {
                    participantMissionInfoList = missionRepository.findByMissionIdInAndStatusNotOrderByCreatedAtDesc(missionIdList);
                }
            }
        }

        List<MissionInfo> missionInfoList = findMissionList(sort, num, filter);

        MainResponse mainResponse = new MainResponse(participantMissionInfoList, missionInfoList);

        return mainResponse;
    }

    private List<MissionInfo> findMissionList(String sort, int num, String filter) {
        if(num < 0) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
        }
        Pageable pageable = PageRequest.of(num, 20);

        if(sort.equals("latest")) {
            if(filter.equals("all")) { // 현재 참여 가능한 모든 미션 목록 최신순으로 정렬
                return missionRepository.findAllByStatusNotCompletedOrderByCreatedAtDesc(pageable);
            } else if(filter.equals("created")) { // 현재 참여 가능한 시작되지 않은 미션 목록 최신순으로 정렬
                return missionRepository.findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc(pageable);
            } else if(filter.equals("started")){ // 현재 참여 가능한 시작된 미션 목록 최신순으로 정렬
                return missionRepository.findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc(pageable);
            } else {
                throw new BadRequestException(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
            }

        } else if(sort.equals("participants")){
            if(filter.equals("all")) { // 현재 참여 가능한 모든 미션 목록 참가인원수로 정렬
                return missionRepository.findAllByStatusNotCompletedOrderByParticipantsDesc(pageable);
            } else if(filter.equals("created")) { // 현재 참여 가능한 시작되지 않은 미션 목록 최신순으로 정렬
                return missionRepository.findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc(pageable);
            } else if(filter.equals("started")) { // 현재 참여 가능한 시작된 미션 목록 최신순으로 정렬
                return missionRepository.findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc(pageable);
            } else {
                throw new BadRequestException(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
            }
        } else {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
        }
    }
}

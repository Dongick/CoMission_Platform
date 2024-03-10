package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.MissionCreateRequest;
import mission.dto.mission.MissionInfoResponse;
import mission.dto.mission.MissionUpdateRequest;
import mission.dto.oauth2.CustomOAuth2User;
import mission.enums.MissionStatus;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void createMission(MissionCreateRequest missionCreateRequest) {
        if (missionCreateRequest == null || missionCreateRequest.getMinParticipants() <= 0) {
            // 에러 처리: 요청이 유효하지 않은 경우
            throw new IllegalArgumentException("Invalid mission create request");
        }

        LocalDateTime now = LocalDateTime.now();

        MissionDocument missionDocument = saveMission(missionCreateRequest, now);

        saveParticipant(missionDocument.getId(), now, missionCreateRequest.getCreatorEmail());

    }

    @Transactional
    public void updateMission(MissionUpdateRequest missionUpdateRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            MissionDocument missionDocument = missionRepository.findByTitle(missionUpdateRequest.getBeforeTitle());

            if(missionDocument.getCreatorEmail() == userEmail) {
                missionDocument.setTitle(missionUpdateRequest.getAfterTitle());
                missionDocument.setDescription(missionUpdateRequest.getDescription());
                missionDocument.setDuration(missionUpdateRequest.getDuration());
                missionDocument.setMinParticipants(missionUpdateRequest.getMinParticipants());
                missionDocument.setFrequency(missionUpdateRequest.getFrequency());

                missionRepository.save(missionDocument);
            }

        }
    }

    @Transactional
    public MissionInfoResponse missionInfo(String title) {
        Boolean participant = false;

        MissionDocument missionDocument = missionRepository.findByTitle(title);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal != null && principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            ParticipantDocument participantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            if(participantDocument != null) {
                participant = true;
            }
        }

        MissionInfoResponse missionInfoResponse = MissionInfoResponse.builder()
                .title(missionDocument.getTitle())
                .description(missionDocument.getDescription())
                .minParticipants(missionDocument.getMinParticipants())
                .participants(missionDocument.getParticipants())
                .frequency(missionDocument.getFrequency())
                .duration(missionDocument.getDuration())
                .status(missionDocument.getStatus())
                .deadline(missionDocument.getDeadline())
                .participant(participant)
                .build();

        return missionInfoResponse;
    }

    private MissionDocument saveMission(MissionCreateRequest request, LocalDateTime now) {
        MissionDocument missionDocument = MissionDocument.builder()
                .createdAt(now)
                .creatorEmail(request.getCreatorEmail())
                .duration(request.getDuration())
                .deadline(request.getMinParticipants() == 1 ? now.toLocalDate().plusDays(request.getDuration()) : null)
                .description(request.getDescription())
                .title(request.getTitle())
                .frequency(request.getFrequency())
                .status(request.getMinParticipants() == 1 ? MissionStatus.STARTED.name() : MissionStatus.CREATED.name())
                .startDate(request.getMinParticipants() == 1 ? LocalDate.from(now) : null)
                .minParticipants(request.getMinParticipants())
                .participants(1)
                .build();

        return missionRepository.save(missionDocument);
    }

    private void saveParticipant(ObjectId missionId, LocalDateTime now, String userEmail) {
        participantRepository.save(ParticipantDocument.builder()
                .missionId(missionId)
                .joinedAt(now)
                .userEmail(userEmail)
                .authentication(new ArrayList<>())
                .build());
    }

    private void endMission(MissionDocument mission) {
        // 미션을 종료 상태로 변경
        mission.setStatus(MissionStatus.COMPLETED.name());
        missionRepository.save(mission);
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void dailyAuthentications() {

        LocalDateTime now = LocalDateTime.now();

        // 모든 미션 가져오기
        List<MissionDocument> missions = missionRepository.findByStatus(MissionStatus.STARTED.name());

        for (MissionDocument mission : missions) {

            if(!(mission.getDeadline().isAfter(now.toLocalDate()))) {

                endMission(mission);
            }
        }
    }
}

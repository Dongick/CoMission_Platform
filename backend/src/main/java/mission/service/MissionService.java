package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.MissionCreateRequest;
import mission.enums.MissionStatus;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Scheduled;
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

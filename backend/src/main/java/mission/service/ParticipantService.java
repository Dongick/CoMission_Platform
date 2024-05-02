package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantRequest;
import mission.enums.MissionStatus;
import mission.exception.BadRequestException;
import mission.exception.ConflictException;
import mission.exception.ErrorCode;
import mission.exception.NotFoundException;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import mission.util.TimeProvider;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;
    private final TimeProvider timeProvider;

    // 미션 참가 매서드
    @Transactional
    public void participateMission(ParticipantRequest participantRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();
        String username = customOAuth2User.getName();

        Optional<MissionDocument> optionalMissionDocument = missionRepository.findById(participantRequest.getId());

        // 미션이 존재하는지 확인
        if(optionalMissionDocument.isEmpty()) {
            throw new NotFoundException(ErrorCode.MISSION_NOT_FOUND, ErrorCode.MISSION_NOT_FOUND.getMessage());
        }

        MissionDocument missionDocument = optionalMissionDocument.get();

        // 해당 미션이 종료된 미션인지 확인
        if(!(missionDocument.getStatus().equals(MissionStatus.COMPLETED.name()))) {

            Optional<ParticipantDocument> optionalParticipantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            // 해당 미션에 참여한 상태인지 확인
            if(optionalParticipantDocument.isPresent()) {
                throw new ConflictException(ErrorCode.ALREADY_PARTICIPATED, ErrorCode.ALREADY_PARTICIPATED.getMessage());
            }

            int participants = missionDocument.getParticipants() + 1;
            missionDocument.setParticipants(participants);

            LocalDateTime now = timeProvider.getCurrentDateTime();

            // 해당 미션의 최소 참여자 수를 충족하면 미션을 시작 상태로 바꿈
            if(participants == missionDocument.getMinParticipants()) {

                handleMissionStarted(missionDocument, now, userEmail, username);
            } else {

                saveParticipant(missionDocument.getId(), now, userEmail, username);
            }

            missionRepository.save(missionDocument);

        } else {
            throw new BadRequestException(ErrorCode.MISSION_ALREADY_COMPLETED, ErrorCode.MISSION_ALREADY_COMPLETED.getMessage());
        }
    }

    // 미션 참여자 저장
    public void saveParticipant(ObjectId missionId, LocalDateTime now, String userEmail, String username) {
        participantRepository.save(ParticipantDocument.builder()
                .missionId(missionId)
                .joinedAt(now)
                .userEmail(userEmail)
                .username(username)
                .authentication(new ArrayList<>())
                .build());
    }

    // 최소 인원수를 만족한 미션을 시작으로 바꿈
    private void handleMissionStarted(MissionDocument missionDocument, LocalDateTime now, String userEmail, String username) {
        LocalDate deadline = now.toLocalDate().plusDays(missionDocument.getDuration());

        missionDocument.setStartDate(LocalDate.from(now));
        missionDocument.setDeadline(deadline);
        missionDocument.setStatus(MissionStatus.STARTED.name());

        saveParticipant(missionDocument.getId(), now, userEmail, username);
    }
}

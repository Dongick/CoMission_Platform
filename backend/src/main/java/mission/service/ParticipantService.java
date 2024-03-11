package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
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

    @Transactional
    public void participateMission(ParticipantRequest participantRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        Optional<MissionDocument> optionalMissionDocument = missionRepository.findByTitle(participantRequest.getTitle());

        if(optionalMissionDocument.isEmpty()) {
            throw new NotFoundException(ErrorCode.MISSION_NOT_FOUND, ErrorCode.MISSION_NOT_FOUND.getMessage());
        }

        MissionDocument missionDocument = optionalMissionDocument.get();

        if(!(missionDocument.getStatus().equals(MissionStatus.COMPLETED.name()))) {

            Optional<ParticipantDocument> optionalParticipantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            if(optionalParticipantDocument.isPresent()) {
                throw new ConflictException(ErrorCode.ALREADY_PARTICIPATED, ErrorCode.ALREADY_PARTICIPATED.getMessage());
            }

            int participants = missionDocument.getParticipants() + 1;
            missionDocument.setParticipants(participants);

            LocalDateTime now = LocalDateTime.now();

            if(participants == missionDocument.getMinParticipants()) {

                handleMissionStarted(missionDocument, now, userEmail);
            } else {

                saveParticipant(missionDocument.getId(), now, userEmail);
            }

            missionRepository.save(missionDocument);

        } else {
            throw new BadRequestException(ErrorCode.MISSION_ALREADY_COMPLETED, ErrorCode.MISSION_ALREADY_COMPLETED.getMessage());
        }
    }

    private void saveParticipant(ObjectId missionId, LocalDateTime now, String userEmail) {
        participantRepository.save(ParticipantDocument.builder()
                .missionId(missionId)
                .joinedAt(now)
                .userEmail(userEmail)
                .authentication(new ArrayList<>())
                .build());
    }

    private void handleMissionStarted(MissionDocument missionDocument, LocalDateTime now, String userEmail) {
        LocalDate deadline = now.toLocalDate().plusDays(missionDocument.getDuration());

        missionDocument.setStartDate(LocalDate.from(now));
        missionDocument.setDeadline(deadline);
        missionDocument.setStatus(MissionStatus.STARTED.name());

        saveParticipant(missionDocument.getId(), now, userEmail);
    }
}

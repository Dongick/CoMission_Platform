package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantRequest;
import mission.enums.MissionStatus;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;

    @Transactional
    public String participateMission(ParticipantRequest participantRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            MissionDocument missionDocument = missionRepository.findByTitle(participantRequest.getTitle());

            if(!(missionDocument.getStatus() == MissionStatus.COMPLETED.name())) {
                int participants = missionDocument.getParticipants() + 1;
                missionDocument.setParticipants(participants);

                LocalDateTime now = LocalDateTime.now();

                if(participants == missionDocument.getMinParticipants()) {

                    handleMissionStarted(missionDocument, now, userEmail);
                } else {

                    saveParticipant(missionDocument.getId(), now, userEmail);
                }

                missionRepository.save(missionDocument);

                return "good";
            } else {
                return "bad";
            }
        } else {
            return "bad";
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

package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.authentication.AuthenticationCreateRequest;
import mission.dto.oauth2.CustomOAuth2User;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;

    @Transactional
    public String createAuthentication(AuthenticationCreateRequest authenticationCreateRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            LocalDateTime now = LocalDateTime.now();

            MissionDocument missionDocument = missionRepository.findByTitle(authenticationCreateRequest.getTitle());

            ParticipantDocument participantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            for(Authentication authentication : participantDocument.getAuthentication()) {
                if(authentication.getDate().isEqual(now.toLocalDate())) {
                    return "bad";
                }
            }

            participantDocument.getAuthentication().add(saveAuthentication(now, authenticationCreateRequest.getPhotoData(), authenticationCreateRequest.getTextData()));

            participantRepository.save(participantDocument);
        }

        return "good";
    }

    private Authentication saveAuthentication(LocalDateTime now, String photoData, String textData) {
        Authentication authentication = Authentication.builder()
                .date(LocalDate.from(now))
                .completed(true)
                .photoData(photoData)
                .textData(textData)
                .build();

        return authentication;
    }
}

package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.authentication.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.NotFoundException;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;

    @Transactional
    public void createAuthentication(AuthenticationCreateRequest authenticationCreateRequest, String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            if(lastAuthentication.getDate().isEqual(now)) {
                throw new BadRequestException(ErrorCode.DUPLICATE_AUTHENTICATION, ErrorCode.DUPLICATE_AUTHENTICATION.getMessage());
            }
        }

        authenticationList.add(saveAuthentication(now, authenticationCreateRequest.getPhotoData(), authenticationCreateRequest.getTextData()));
        participantRepository.save(participantDocument);
    }

    @Transactional
    public void updateAuthentication(AuthenticationUpdateRequest authenticationUpdateRequest, String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            if(lastAuthentication.getDate().isEqual(now)) {
                lastAuthentication.setPhotoData(authenticationUpdateRequest.getPhotoData());
                lastAuthentication.setTextData(authenticationUpdateRequest.getTextData());

                participantRepository.save(participantDocument);
            } else {
                throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());

            }
        } else {
            throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());
        }
    }

    @Transactional
    public void deleteAuthentication(String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            if(lastAuthentication.getDate().isEqual(now)) {
                authenticationList.remove(authenticationList.size() - 1);

                participantRepository.save(participantDocument);
            } else {
                throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());

            }
        } else {
            throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());

        }
    }

    public AuthenticationListResponse authenticationList(String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        getParticipantDocument(missionDocument, userEmail);

        List<ParticipantDocument> participantDocumentList = participantRepository.findByMissionId(missionDocument.getId());

        Map<LocalDate, List<Map<String, Object>>> result = groupAndSortAuthentications(participantDocumentList);

        return new AuthenticationListResponse(result);
    }

    public Map<LocalDate, List<Map<String, Object>>> groupAndSortAuthentications(List<ParticipantDocument> participantDocumentList) {

        return participantDocumentList.stream()
                .flatMap(participant -> participant.getAuthentication().stream()
                        .map(authentication -> {
                            Map<String, Object> authenticationMap = new HashMap<>();
                            authenticationMap.put("date", authentication.getDate());
                            authenticationMap.put("photoData", authentication.getPhotoData());
                            authenticationMap.put("textData", authentication.getTextData());
                            authenticationMap.put("userEmail", participant.getUserEmail());
                            return new Object[]{authentication.getDate(), authenticationMap};
                        })
                )
                .sorted(Comparator.comparing(o -> ((LocalDate) o[0])))
                .collect(Collectors.groupingBy(
                        o -> ((LocalDate) o[0]),
                        Collectors.mapping(o -> (Map<String, Object>) o[1], Collectors.toList())
                ));
    }

    private Authentication saveAuthentication(LocalDate now, String photoData, String textData) {
        return Authentication.builder()
                .date(now)
                .completed(true)
                .photoData(photoData)
                .textData(textData)
                .build();
    }

    private MissionDocument getMissionDocument(String title) {
        return missionRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MISSION_NOT_FOUND, ErrorCode.MISSION_NOT_FOUND.getMessage()));
    }

    private ParticipantDocument getParticipantDocument(MissionDocument missionDocument, String userEmail) {
        return participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND, ErrorCode.PARTICIPANT_NOT_FOUND.getMessage()));
    }
}

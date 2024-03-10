package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.authentication.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            LocalDate now = LocalDate.now();

            MissionDocument missionDocument = missionRepository.findByTitle(authenticationCreateRequest.getTitle());

            ParticipantDocument participantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            List<Authentication> authenticationList = participantDocument.getAuthentication();

            if(!authenticationList.isEmpty()) {
                Authentication lastAuthentication = participantDocument.getAuthentication().get(authenticationList.size() - 1);

                if(lastAuthentication.getDate().isEqual(now)) {
                    return "bad";
                }
            }

            authenticationList.add(saveAuthentication(now, authenticationCreateRequest.getPhotoData(), authenticationCreateRequest.getTextData()));
            participantRepository.save(participantDocument);

        }

        return "good";
    }

    @Transactional
    public void updateAuthentication(AuthenticationUpdateRequest authenticationUpdateRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            LocalDate now = LocalDate.now();

            MissionDocument missionDocument = missionRepository.findByTitle(authenticationUpdateRequest.getTitle());

            ParticipantDocument participantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            List<Authentication> authenticationList = participantDocument.getAuthentication();

            if(!authenticationList.isEmpty()) {
                Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

                if(lastAuthentication.getDate().isEqual(now)) {
                    lastAuthentication.setPhotoData(authenticationUpdateRequest.getPhotoData());
                    lastAuthentication.setTextData(authenticationUpdateRequest.getTextData());

                    participantRepository.save(participantDocument);
                }
            }

        }
    }

    @Transactional
    public void deleteAuthentication(AuthenticationDeleteRequest authenticationDeleteRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            LocalDate now = LocalDate.now();

            MissionDocument missionDocument = missionRepository.findByTitle(authenticationDeleteRequest.getTitle());

            ParticipantDocument participantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            List<Authentication> authenticationList = participantDocument.getAuthentication();

            if(!authenticationList.isEmpty()) {
                Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

                if(lastAuthentication.getDate().isEqual(now)) {
                    authenticationList.remove(authenticationList.size() - 1);

                    participantRepository.save(participantDocument);
                }
            }
        }
    }

    public AuthenticationListResponse authenticationList(String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            LocalDate now = LocalDate.now();

            MissionDocument missionDocument = missionRepository.findByTitle(title);

            List<ParticipantDocument> participantDocumentList = participantRepository.findByMissionId(missionDocument.getId());


            Map<LocalDate, List<Map<String, Object>>> result = groupAndSortAuthentications(participantDocumentList);
            System.out.println(result);

            return new AuthenticationListResponse(result);
        } else {
            return null;
        }
    }

    public Map<LocalDate, List<Map<String, Object>>> groupAndSortAuthentications(List<ParticipantDocument> participantDocumentList) {
//        return participantDocumentList.stream()
//                .flatMap(participant -> participant.getAuthentication().stream()
//                        .map(authentication -> new Object[]{participant.getUserEmail(), authentication})
//                )
//                .sorted(Comparator.comparing(o -> ((Authentication) o[1]).getDate()))
//                .collect(Collectors.groupingBy(
//                        o -> ((Authentication) o[1]).getDate(),
//                        Collectors.groupingBy(o -> (String) o[0], Collectors.mapping(o -> (Authentication) o[1], Collectors.toList()))
//                ));

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
        Authentication authentication = Authentication.builder()
                .date(now)
                .completed(true)
                .photoData(photoData)
                .textData(textData)
                .build();

        return authentication;
    }
}

package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.authentication.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.enums.MissionStatus;
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

    // 인증글 생성 매서드
    @Transactional
    public void createAuthentication(AuthenticationCreateRequest authenticationCreateRequest, String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        judgeMissionStatus(missionDocument.getStatus());

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 이미 작성했는지 확인
            if(lastAuthentication.getDate().isEqual(now)) {
                throw new BadRequestException(ErrorCode.DUPLICATE_AUTHENTICATION, ErrorCode.DUPLICATE_AUTHENTICATION.getMessage());
            }
        }

        authenticationList.add(saveAuthentication(now, authenticationCreateRequest.getPhotoData(), authenticationCreateRequest.getTextData()));
        participantRepository.save(participantDocument);
    }

    // 인증글 수정 매서드
    @Transactional
    public void updateAuthentication(AuthenticationUpdateRequest authenticationUpdateRequest, String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        judgeMissionStatus(missionDocument.getStatus());

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 작성했는지 확인
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

    // 인증글 삭제 매서드
    @Transactional
    public void deleteAuthentication(String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDate now = LocalDate.now();

        MissionDocument missionDocument = getMissionDocument(title);

        judgeMissionStatus(missionDocument.getStatus());

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 작성했는지 확인
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

    // 해당 미션의 모든 인증글 보기 매서드
    public AuthenticationListResponse authenticationList(String title) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        MissionDocument missionDocument = getMissionDocument(title);

        // 해당 미션의 상태 확인
        if(missionDocument.getStatus().equals(MissionStatus.CREATED.name())) {
            throw new BadRequestException(ErrorCode.MISSION_NOT_STARTED, ErrorCode.MISSION_NOT_STARTED.getMessage());
        }

        getParticipantDocument(missionDocument, userEmail);

        List<ParticipantDocument> participantDocumentList = participantRepository.findByMissionId(missionDocument.getId());

        Map<LocalDate, List<Map<String, Object>>> result = groupAndSortAuthentications(participantDocumentList);

        return new AuthenticationListResponse(result);
    }

    // 인증글들을 형식에 맞춰 출력
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

    // 인증글 저장
    private Authentication saveAuthentication(LocalDate now, String photoData, String textData) {
        return Authentication.builder()
                .date(now)
                .completed(true)
                .photoData(photoData)
                .textData(textData)
                .build();
    }

    // 해당 미션이 존재하는지 확인
    private MissionDocument getMissionDocument(String title) {
        return missionRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MISSION_NOT_FOUND, ErrorCode.MISSION_NOT_FOUND.getMessage()));
    }

    // 해당 미션에 해당 참가자가 존재하는지 확인
    private ParticipantDocument getParticipantDocument(MissionDocument missionDocument, String userEmail) {
        return participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND, ErrorCode.PARTICIPANT_NOT_FOUND.getMessage()));
    }

    // 해당 미션의 상태 확인
    private void judgeMissionStatus(String status) {
        if(status.equals(MissionStatus.CREATED.name())) {
            throw new BadRequestException(ErrorCode.MISSION_NOT_STARTED, ErrorCode.MISSION_NOT_STARTED.getMessage());

        } else if(status.equals(MissionStatus.COMPLETED.name())){
            throw new BadRequestException(ErrorCode.MISSION_ALREADY_COMPLETED, ErrorCode.MISSION_ALREADY_COMPLETED.getMessage());
        }
    }
}

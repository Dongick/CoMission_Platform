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
<<<<<<< HEAD
=======
import mission.exception.ForbiddenException;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import mission.exception.NotFoundException;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
<<<<<<< HEAD
=======
import java.time.LocalDateTime;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;
    private final FileService fileService;

    // 인증글 생성 매서드
    @Transactional
    public void createAuthentication(AuthenticationCreateRequest authenticationCreateRequest, MultipartFile file, String title) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

<<<<<<< HEAD
        LocalDate now = LocalDate.now();
=======
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue();

        LocalDate startDate = LocalDate.from(now.minusDays(dayOfWeek - 1));
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        MissionDocument missionDocument = getMissionDocument(title);

        judgeMissionStatus(missionDocument.getStatus());

<<<<<<< HEAD
=======
        int authCount = getAuthenticationCount(missionDocument.getFrequency());

>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

<<<<<<< HEAD
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 이미 작성했는지 확인
            if(lastAuthentication.getDate().isEqual(now)) {
=======
        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {

            // 이번주에 사용자가 해당 미션에 작성한 인증글 확인
            List<Authentication> dayOfWeekAuthenticationList = authenticationList.stream()
                    .filter(auth -> !auth.getDate().toLocalDate().isBefore(startDate) && !auth.getDate().toLocalDate().isAfter(LocalDate.from(now)))
                    .collect(Collectors.toList());

            // 이번주에 작성한 인증글의 횟수가 이번주에 허용된 작성 횟수를 초과했는지 확인
            if(dayOfWeekAuthenticationList.size() == authCount) {
                throw new ForbiddenException(ErrorCode.EXCEEDED_AUTHENTICATION_LIMIT, ErrorCode.EXCEEDED_AUTHENTICATION_LIMIT.getMessage());
            }

            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 이미 작성했는지 확인
            if(LocalDate.from(lastAuthentication.getDate()).isEqual(LocalDate.from(now))) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
                throw new BadRequestException(ErrorCode.DUPLICATE_AUTHENTICATION, ErrorCode.DUPLICATE_AUTHENTICATION.getMessage());
            }
        }

<<<<<<< HEAD
        String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadFile(file);
=======
        // 인증 사진을 서버에 저장
        String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadAuthenticationFile(file);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        authenticationList.add(saveAuthentication(now, fileLocation, authenticationCreateRequest.getTextData()));
        participantRepository.save(participantDocument);
    }

    // 인증글 수정 매서드
    @Transactional
    public void updateAuthentication(AuthenticationUpdateRequest authenticationUpdateRequest, MultipartFile file, String title) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

<<<<<<< HEAD
        LocalDate now = LocalDate.now();
=======
        LocalDateTime now = LocalDateTime.now();
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        MissionDocument missionDocument = getMissionDocument(title);

        judgeMissionStatus(missionDocument.getStatus());

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 작성했는지 확인
<<<<<<< HEAD
            if(lastAuthentication.getDate().isEqual(now)) {
=======
            if(LocalDate.from(lastAuthentication.getDate()).isEqual(LocalDate.from(now))) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

                // 기존 인증글에 사진 데이터가 존재하면 삭제
                if (lastAuthentication.getPhotoData() != null) {
                    fileService.deleteFile(lastAuthentication.getPhotoData());
                }

<<<<<<< HEAD
                String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadFile(file);
=======
                // 인증글에 사진이 존재하면 서버에 저장
                String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadAuthenticationFile(file);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

                lastAuthentication.setPhotoData(fileLocation);
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
    public void deleteAuthentication(String title) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

<<<<<<< HEAD
        LocalDate now = LocalDate.now();
=======
        LocalDateTime now = LocalDateTime.now();
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        MissionDocument missionDocument = getMissionDocument(title);

        judgeMissionStatus(missionDocument.getStatus());

        ParticipantDocument participantDocument = getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 작성했는지 확인
<<<<<<< HEAD
            if(lastAuthentication.getDate().isEqual(now)) {
=======
            if(LocalDate.from(lastAuthentication.getDate()).isEqual(LocalDate.from(now))) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

                if (lastAuthentication.getPhotoData() != null) {
                    fileService.deleteFile(lastAuthentication.getPhotoData());
                }

                authenticationList.remove(authenticationList.size() - 1);

                participantRepository.save(participantDocument);
            } else {
                throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());

            }
        } else {
            throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());

        }
    }

<<<<<<< HEAD
    // 해당 미션의 모든 인증글 보기 매서드
    public AuthenticationListResponse authenticationList(String title) {
=======
    // 해당 미션의 인증글 보기 매서드
    @Transactional
    public AuthenticationListResponse authenticationList(String title, int num) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
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

<<<<<<< HEAD
        Map<LocalDate, List<Map<String, Object>>> result = groupAndSortAuthentications(participantDocumentList);

        return new AuthenticationListResponse(result);
    }
    // 인증글들을 형식에 맞춰 출력
    public Map<LocalDate, List<Map<String, Object>>> groupAndSortAuthentications(List<ParticipantDocument> participantDocumentList) {
=======
        List<Map<String, Object>> allAuthenticationList = groupAndSortAuthentications(participantDocumentList);

        List<Map<String, Object>> authenticationList = null;

        // 인증글들을 lazy loading 으로 처리
        if(allAuthenticationList.size() > 20 * num) {
            if(allAuthenticationList.size() >= 20 * (num + 1)) {
                authenticationList = allAuthenticationList.subList(20*num, 20*(num+1));
            } else {
                int endIndex = allAuthenticationList.size();

                authenticationList = allAuthenticationList.subList(20*num, endIndex);
            }

            for(Map<String, Object> authentication : authenticationList) {

                authentication.put("date", LocalDate.from((LocalDateTime) authentication.get("date")));
            }
        }

        return new AuthenticationListResponse(authenticationList);
    }

    // 인증글들을 형식에 맞춰 출력
    public List<Map<String, Object>> groupAndSortAuthentications(List<ParticipantDocument> participantDocumentList) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        return participantDocumentList.stream()
                .flatMap(participant -> participant.getAuthentication().stream()
                        .map(authentication -> {
                            Map<String, Object> authenticationMap = new HashMap<>();
                            authenticationMap.put("date", authentication.getDate());
                            authenticationMap.put("photoData", authentication.getPhotoData());
                            authenticationMap.put("textData", authentication.getTextData());
                            authenticationMap.put("userEmail", participant.getUserEmail());
<<<<<<< HEAD
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
=======
                            return authenticationMap;
                        })
                )
                .sorted(Comparator.comparing(map -> (LocalDateTime) map.get("date"), Comparator.reverseOrder()))
                .collect(Collectors.toList()
                );
    }

    // 인증글 저장
    private Authentication saveAuthentication(LocalDateTime now, String photoData, String textData) {
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
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
<<<<<<< HEAD
=======

    // 해당 미션의 인증글 작성이 주 몇회인지 확인
    private int getAuthenticationCount(String frequency) {
        int authCount = 0;

        switch (frequency) {
            case "매일" : authCount = 7;
                break;
            case "주1회" : authCount = 1;
                break;
            case "주2회" : authCount = 2;
                break;
            case "주3회" : authCount = 3;
                break;
            case "주4회" : authCount = 4;
                break;
            case "주5회" : authCount = 5;
                break;
            case "주6회" : authCount = 6;
                break;
        }

        return authCount;
    }
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
}

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
import mission.exception.ForbiddenException;
import mission.exception.NotFoundException;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import mission.util.TimeProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ParticipantRepository participantRepository;
    private final AWSS3Service awss3Service;
    private final FileService fileService;
    private final TimeProvider timeProvider;
    private final MissionService missionService;
    private final UserService userService;
    private static final String AUTHENTICATION_DIR = "authentications/";

    // 인증글 생성 매서드
    @Transactional
    public void createAuthentication(AuthenticationCreateRequest authenticationCreateRequest, MultipartFile file, String id) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDateTime now = timeProvider.getCurrentDateTime();
        int dayOfWeek = now.getDayOfWeek().getValue();

        LocalDate startDate = LocalDate.from(now.minusDays(dayOfWeek - 1));

        // 해당 미션이 존재하는지 확인
        MissionDocument missionDocument = missionService.getMissionDocument(id);

        judgeMissionStatus(missionDocument.getStatus());

        int authCount = getAuthenticationCount(missionDocument.getFrequency());

        // 해당 미션에 해당 참가자가 존재하는지 확인
        ParticipantDocument participantDocument = userService.getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

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
                throw new BadRequestException(ErrorCode.DUPLICATE_AUTHENTICATION, ErrorCode.DUPLICATE_AUTHENTICATION.getMessage());
            }
        }

        // 인증 사진을 AWS S3에 저장
//        String fileLocation = file == null || file.isEmpty() ? null : awss3Service.uploadFile(file, AUTHENTICATION_DIR);
        String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadFile(file, AUTHENTICATION_DIR);

        authenticationList.add(saveAuthentication(now, fileLocation, authenticationCreateRequest.getTextData()));
        participantRepository.save(participantDocument);
    }

    // 인증글 수정 매서드
    @Transactional
    public void updateAuthentication(AuthenticationUpdateRequest authenticationUpdateRequest, MultipartFile file, String id) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDateTime now = timeProvider.getCurrentDateTime();

        // 해당 미션이 존재하는지 확인
        MissionDocument missionDocument = missionService.getMissionDocument(id);

        judgeMissionStatus(missionDocument.getStatus());

        // 해당 미션에 해당 참가자가 존재하는지 확인
        ParticipantDocument participantDocument = userService.getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 작성했는지 확인
            if(LocalDate.from(lastAuthentication.getDate()).isEqual(LocalDate.from(now))) {

                // 기존 인증글에 사진 데이터가 존재하면 삭제
                if (lastAuthentication.getPhotoData() != null) {
//                    awss3Service.deleteFile(lastAuthentication.getPhotoData(), AUTHENTICATION_DIR);
                    fileService.deleteFile(lastAuthentication.getPhotoData());
                }

                // 인증글에 사진이 존재하면 AWS S3에 저장
//                String fileLocation = file == null || file.isEmpty() ? null : awss3Service.uploadFile(file, AUTHENTICATION_DIR);
                String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadFile(file, AUTHENTICATION_DIR);

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
    public void deleteAuthentication(String id) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDateTime now = timeProvider.getCurrentDateTime();

        // 해당 미션이 존재하는지 확인
        MissionDocument missionDocument = missionService.getMissionDocument(id);

        judgeMissionStatus(missionDocument.getStatus());

        // 해당 미션에 해당 참가자가 존재하는지 확인
        ParticipantDocument participantDocument = userService.getParticipantDocument(missionDocument, userEmail);

        List<Authentication> authenticationList = participantDocument.getAuthentication();

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {
            Authentication lastAuthentication = authenticationList.get(authenticationList.size() - 1);

            // 당일 인증글을 작성했는지 확인
            if(LocalDate.from(lastAuthentication.getDate()).isEqual(LocalDate.from(now))) {

                // 당일 인증글에 사진 데이터가 존재하면 삭제
                if (lastAuthentication.getPhotoData() != null) {
//                    awss3Service.deleteFile(lastAuthentication.getPhotoData(), AUTHENTICATION_DIR);
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

    // 해당 미션의 인증글 보기 매서드
    @Transactional
    public AuthenticationListResponse authenticationList(String id, int num) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        // 해당 미션이 존재하는지 확인
        MissionDocument missionDocument = missionService.getMissionDocument(id);

        // 해당 미션의 상태 확인
        if(missionDocument.getStatus().equals(MissionStatus.CREATED.name())) {
            throw new BadRequestException(ErrorCode.MISSION_NOT_STARTED, ErrorCode.MISSION_NOT_STARTED.getMessage());
        }

        // 해당 미션에 해당 참가자가 존재하는지 확인
        userService.getParticipantDocument(missionDocument, userEmail);

        List<ParticipantDocument> participantDocumentList = participantRepository.findByMissionId(missionDocument.getId());

        List<Map<String, Object>> allAuthenticationList = groupAndSortAuthentications(participantDocumentList, num);

        return new AuthenticationListResponse(allAuthenticationList);
    }

    // 인증글들을 형식에 맞춰 출력
    private List<Map<String, Object>> groupAndSortAuthentications(List<ParticipantDocument> participantDocumentList, int num) {

        return participantDocumentList.stream()
                .flatMap(participant -> participant.getAuthentication().stream()
                        .map(authentication -> {
                            Map<String, Object> authenticationMap = new HashMap<>();
                            authenticationMap.put("date", authentication.getDate());
                            authenticationMap.put("photoData", authentication.getPhotoData());
                            authenticationMap.put("textData", authentication.getTextData());
                            authenticationMap.put("userEmail", participant.getUserEmail());
                            authenticationMap.put("username", participant.getUsername());
                            return authenticationMap;
                        })
                )
                .sorted(Comparator.comparing(map -> (LocalDateTime) map.get("date"), Comparator.reverseOrder()))
                .skip((long) num * 5)
                .limit(5)
                .collect(Collectors.toList()
                );
    }

    // 인증글 저장
    private Authentication saveAuthentication(LocalDateTime now, String photoData, String textData) {
        return Authentication.builder()
                .date(now)
                .completed(true)
                .photoData(photoData)
                .textData(textData)
                .build();
    }

    // 해당 미션의 상태 확인
    private void judgeMissionStatus(String status) {
        if(status.equals(MissionStatus.CREATED.name())) {
            throw new BadRequestException(ErrorCode.MISSION_NOT_STARTED, ErrorCode.MISSION_NOT_STARTED.getMessage());

        } else if(status.equals(MissionStatus.COMPLETED.name())){
            throw new BadRequestException(ErrorCode.MISSION_ALREADY_COMPLETED, ErrorCode.MISSION_ALREADY_COMPLETED.getMessage());
        }
    }

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
}

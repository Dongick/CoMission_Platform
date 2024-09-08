package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.AuthenticationDocument;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.authentication.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.enums.MissionStatus;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.ForbiddenException;
import mission.exception.NotFoundException;
import mission.repository.AuthenticationRepository;
import mission.util.TimeProvider;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AWSS3Service awss3Service;
    private final FileService fileService;
    private final TimeProvider timeProvider;
    private final MissionService missionService;
    private final UserService userService;
    private static final String AUTHENTICATION_DIR = "authentications/";
    private final AuthenticationRepository authenticationRepository;

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

        List<AuthenticationDocument> authenticationList = authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(participantDocument.getId(), participantDocument.getMissionId(), startDate.atStartOfDay(), now);

        // 기존에 작성한 인증글이 존재하는지 확인
        if(!authenticationList.isEmpty()) {

            // 이번주에 작성한 인증글의 횟수가 이번주에 허용된 작성 횟수를 초과했는지 확인
            if(authenticationList.size() == authCount) {
                throw new ForbiddenException(ErrorCode.EXCEEDED_AUTHENTICATION_LIMIT, ErrorCode.EXCEEDED_AUTHENTICATION_LIMIT.getMessage());
            }

            // 당일 인증글을 이미 작성했는지 확인
            if(LocalDate.from(authenticationList.get(authenticationList.size() - 1).getDate()).isEqual(LocalDate.from(now))) {
                throw new BadRequestException(ErrorCode.DUPLICATE_AUTHENTICATION, ErrorCode.DUPLICATE_AUTHENTICATION.getMessage());
            }
        }

        // 인증 사진을 AWS S3에 저장
        String fileLocation = file == null || file.isEmpty() ? null : awss3Service.uploadFile(file, AUTHENTICATION_DIR);
//        String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadFile(file, AUTHENTICATION_DIR);

        saveAuthentication(participantDocument.getUserEmail(), participantDocument.getUsername(), participantDocument.getMissionId(), participantDocument.getId(), now, fileLocation, authenticationCreateRequest.getTextData());
    }

    // 인증글 수정 매서드
    @Transactional
    public void updateAuthentication(AuthenticationUpdateRequest authenticationUpdateRequest, MultipartFile file, String id) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDateTime now = timeProvider.getCurrentDateTime();

        LocalDateTime startOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MIDNIGHT);
        LocalDateTime endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);

        // 해당 미션이 존재하는지 확인
        MissionDocument missionDocument = missionService.getMissionDocument(id);

        judgeMissionStatus(missionDocument.getStatus());

        // 해당 미션에 해당 참가자가 존재하는지 확인
        ParticipantDocument participantDocument = userService.getParticipantDocument(missionDocument, userEmail);

        Optional<AuthenticationDocument> authenticationDocumentOptional = authenticationRepository.findByParticipantIdAndMissionIdAndDate(participantDocument.getId(), participantDocument.getMissionId(), startOfDay, endOfDay);

        // 당일 인증글을 작성했는지 확인
        if(authenticationDocumentOptional.isEmpty()) {
            throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());
        }

        AuthenticationDocument authenticationDocument = authenticationDocumentOptional.get();

        // 기존 인증글에 사진 데이터가 존재하면 삭제
        if (authenticationDocument.getPhotoData() != null) {
            awss3Service.deleteFile(authenticationDocument.getPhotoData(), AUTHENTICATION_DIR);
//            fileService.deleteFile(authenticationDocument.getPhotoData());
        }

        // 인증글에 사진이 존재하면 AWS S3에 저장
        String fileLocation = file == null || file.isEmpty() ? null : awss3Service.uploadFile(file, AUTHENTICATION_DIR);
//        String fileLocation = file == null || file.isEmpty() ? null : fileService.uploadFile(file, AUTHENTICATION_DIR);

        authenticationDocument.setPhotoData(fileLocation);
        authenticationDocument.setTextData(authenticationUpdateRequest.getTextData());

        authenticationRepository.save(authenticationDocument);


    }

    // 인증글 삭제 매서드
    @Transactional
    public void deleteAuthentication(String id) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        LocalDateTime now = timeProvider.getCurrentDateTime();

        LocalDateTime startOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MIDNIGHT);
        LocalDateTime endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);


        // 해당 미션이 존재하는지 확인
        MissionDocument missionDocument = missionService.getMissionDocument(id);

        judgeMissionStatus(missionDocument.getStatus());

        // 해당 미션에 해당 참가자가 존재하는지 확인
        ParticipantDocument participantDocument = userService.getParticipantDocument(missionDocument, userEmail);

        Optional<AuthenticationDocument> authenticationDocumentOptional = authenticationRepository.findByParticipantIdAndMissionIdAndDate(participantDocument.getId(), participantDocument.getMissionId(), startOfDay, endOfDay);

        // 당일 인증글을 작성했는지 확인
        if(authenticationDocumentOptional.isEmpty()) {
            throw new NotFoundException(ErrorCode.AUTHENTICATION_NOT_FOUND, ErrorCode.AUTHENTICATION_NOT_FOUND.getMessage());
        }

        AuthenticationDocument authenticationDocument = authenticationDocumentOptional.get();


        // 당일 인증글에 사진 데이터가 존재하면 삭제
        if (authenticationDocument.getPhotoData() != null) {
            awss3Service.deleteFile(authenticationDocument.getPhotoData(), AUTHENTICATION_DIR);
//            fileService.deleteFile(authenticationDocument.getPhotoData());
        }

        authenticationRepository.delete(authenticationDocument);
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

        Pageable pageable = PageRequest.of(num, 5, Sort.by(Sort.Direction.DESC, "date"));

        Page<AuthenticationDocument> authenticationDocumentPage = authenticationRepository.findByMissionId(missionDocument.getId(), pageable);

        List<AuthenticationList> authenticationListList = authenticationDocumentPage.getContent().stream()
                .map(doc -> AuthenticationList.builder()
                        .username(doc.getUsername())
                        .userEmail(doc.getUserEmail())
                        .date(doc.getDate())
                        .completed(doc.isCompleted())
                        .photoData(doc.getPhotoData())
                        .textData(doc.getTextData())
                        .build())
                .collect(Collectors.toList());

        return new AuthenticationListResponse(authenticationListList);
    }

    private void saveAuthentication(String userEmail, String username, ObjectId missionId, ObjectId participantId, LocalDateTime now, String photoData, String textData) {
        AuthenticationDocument authenticationDocument = AuthenticationDocument.builder()
                .userEmail(userEmail)
                .username(username)
                .missionId(missionId)
                .participantId(participantId)
                .date(now)
                .completed(true)
                .photoData(photoData)
                .textData(textData)
                .build();

        authenticationRepository.save(authenticationDocument);
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
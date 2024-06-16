package mission.service;

import mission.document.AuthenticationDocument;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.authentication.AuthenticationCreateRequest;
import mission.dto.authentication.AuthenticationList;
import mission.dto.authentication.AuthenticationListResponse;
import mission.dto.authentication.AuthenticationUpdateRequest;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.User;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.ForbiddenException;
import mission.exception.NotFoundException;
import mission.repository.AuthenticationRepository;
import mission.repository.ParticipantRepository;
import mission.util.TimeProvider;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private AWSS3Service awss3Service;
    @Mock
    private FileService fileService;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private MissionService missionService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationRepository authenticationRepository;
    private static String AUTHENTICATION_DIR = "authentications/";
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("createAuthentication 매서드: 기존에 작성한 인증글이 존재하지 않고 이미지 파일을 가진 인증글 생성 성공")
    void createAuthentication_withoutAuthentication_withMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );
        String fileLocation = "test/photo.jpg";
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");

        List<AuthenticationDocument> authenticationDocumentList = new ArrayList<>();

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);
        when(fileService.uploadFile(photoData, AUTHENTICATION_DIR)).thenReturn(fileLocation);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);

        // when
        authenticationService.createAuthentication(authenticationCreateRequest, photoData, missionId);

        // then
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService).uploadFile(photoData, AUTHENTICATION_DIR);
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).save(any(AuthenticationDocument.class));
        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));

//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//
//        Assertions.assertThat(authenticationList.size()).isEqualTo(1);
//        AuthenticationDocument authentication = authenticationList.get(0);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
//        Assertions.assertThat(authentication.getPhotoData()).isEqualTo(fileLocation);
    }

    @Test
    @DisplayName("createAuthentication 매서드: 이번주에 작성한 인증글이 아닌 그 전에 작성한 인증글이 존재하고 이미지 파일이 없는 인증글 생성 성공")
    void createAuthentication_withoutWeekAuthentication_withoutMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();
        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);
        int dayOfWeek = now.getDayOfWeek().getValue();
        LocalDate startDate = LocalDate.from(now.minusDays(dayOfWeek - 1));

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareTwoAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now.minusDays(7), null, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        List<AuthenticationDocument> authenticationDocumentList = prepareTwoAuthentication(now.minusDays(7), null, textData);

        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.createAuthentication(authenticationCreateRequest, null, missionId);

        // then
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(awss3Service, never()).uploadFile(any(MultipartFile.class), anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).save(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(3);
//        List<AuthenticationDocument> dayOfWeekAuthenticationList = authenticationList.stream()
//                .filter(auth -> !auth.getDate().toLocalDate().isBefore(startDate) && !auth.getDate().toLocalDate().isAfter(LocalDate.from(now)))
//                .collect(Collectors.toList());
//        Assertions.assertThat(dayOfWeekAuthenticationList.size()).isEqualTo(1);
//        AuthenticationDocument authentication = dayOfWeekAuthenticationList.get(0);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
//        Assertions.assertThat(authentication.getPhotoData()).isNull();
    }

    @Test
    @DisplayName("createAuthentication 매서드: 기존에 이번주에 작성한 인증글이 존재 성공")
    void createAuthentication_withWeekAuthentication_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();
        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);
        int dayOfWeek = now.getDayOfWeek().getValue();
        LocalDate startDate = LocalDate.from(now.minusDays(dayOfWeek - 1));

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareTwoAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, null, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        List<AuthenticationDocument> authenticationDocumentList = prepareTwoAuthentication(now, null, textData);



        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.createAuthentication(authenticationCreateRequest, null, missionId);

        // then
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(awss3Service, never()).uploadFile(any(MultipartFile.class), anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).save(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(3);
//        List<AuthenticationDocument> dayOfWeekAuthenticationList = authenticationList.stream()
//                .filter(auth -> !auth.getDate().toLocalDate().isBefore(startDate) && !auth.getDate().toLocalDate().isAfter(LocalDate.from(now)))
//                .collect(Collectors.toList());
//        Assertions.assertThat(dayOfWeekAuthenticationList.size()).isEqualTo(3);
//        AuthenticationDocument authentication = dayOfWeekAuthenticationList.get(2);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
    }

    @Test
    @DisplayName("createAuthentication 매서드: 인증글을 작성하려는 미션이 존재하지 않아 실패")
    void createAuthentication_MissionNotFound_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String missionId = "65ea0c8007b2c737d6227bf0";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionService.getMissionDocument(anyString())).thenThrow(new NotFoundException(ErrorCode.MISSION_NOT_FOUND, ErrorCode.MISSION_NOT_FOUND.getMessage()));

        // when, then
        assertThrows(NotFoundException.class, () -> authenticationService.createAuthentication(authenticationCreateRequest, null, missionId));
    }

    @Test
    @DisplayName("createAuthentication 매서드: 인증글을 작성하려는 미션이 아직 시작하지 않아 실패")
    void createAuthentication_MissionNotStarted_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String missionId = "65ea0c8007b2c737d6227bf0";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "CREATED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);

        // when, then
        assertThrows(BadRequestException.class, () -> authenticationService.createAuthentication(authenticationCreateRequest, null, missionId));
    }

    @Test
    @DisplayName("createAuthentication 매서드: 인증글을 작성하려는 미션이 종료되어서 실패")
    void createAuthentication_MissionAlreadyCompleted_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String missionId = "65ea0c8007b2c737d6227bf0";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "COMPLETED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);

        // when, then
        assertThrows(BadRequestException.class, () -> authenticationService.createAuthentication(authenticationCreateRequest, null, missionId));
    }

    @Test
    @DisplayName("createAuthentication 매서드: 해당 사용자가 해당 미션에 참여하지 않아 실패")
    void createAuthentication_ParticipantNotFound_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String missionId = "65ea0c8007b2c737d6227bf0";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenThrow(new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND, ErrorCode.PARTICIPANT_NOT_FOUND.getMessage()));

        // when, then
        assertThrows(NotFoundException.class, () -> authenticationService.createAuthentication(authenticationCreateRequest, null, missionId));
    }

    @Test
    @DisplayName("createAuthentication 매서드: 이번주에 작성한 인증글의 횟수가 이번주에 허용된 작성 횟수를 초과하여 실패")
    void createAuthentication_ExceededAuthenticationLimit_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주2회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareTwoAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, null, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");

        List<AuthenticationDocument> authenticationDocumentList = prepareTwoAuthentication(now, null, textData);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when, then
        assertThrows(ForbiddenException.class, () -> authenticationService.createAuthentication(authenticationCreateRequest, null, missionId));
    }

    @Test
    @DisplayName("createAuthentication 매서드: 당일 인증글을 이미 작성하여 실패")
    void createAuthentication_DuplicateAuthentication_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData(textData);
        String frequency = "주2회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareParticipantDocument(missionId, "test", "test@example.com");

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");

        List<AuthenticationDocument> authenticationList = new ArrayList<>();

        authenticationList.add(AuthenticationDocument.builder()
                .date(now)
                .build());

//        participantDocument.setAuthentication(authenticationList);

        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDateRange(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationList);


        // when, then
        assertThrows(BadRequestException.class, () -> authenticationService.createAuthentication(authenticationCreateRequest, null, missionId));
    }

    @Test
    @DisplayName("updateAuthentication 매서드: 수정 전 이미지 파일 존재, 수정 이미지 파일 존재 성공")
    void updateAuthentication_withBeforeMultipartFile_withAfterMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData(textData);
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );
        String fileLocation = "test/photo.jpg";
        String frequency = "주3회";
        String status = "STARTED";
        String photoUrl = "photoUrl.jpg";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, photoUrl, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        Optional<AuthenticationDocument> authenticationDocumentList = prepareNotListOneAuthentication(now, photoUrl, textData);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);
        when(fileService.uploadFile(photoData, AUTHENTICATION_DIR)).thenReturn(fileLocation);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.updateAuthentication(authenticationUpdateRequest, photoData, missionId);

        // then
        verify(timeProvider).getCurrentDateTime();
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService).deleteFile(anyString());
        verify(fileService).uploadFile(any(MultipartFile.class), anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).save(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(1);
//        AuthenticationDocument authentication = authenticationList.get(0);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
//        Assertions.assertThat(authentication.getPhotoData()).isEqualTo(fileLocation);

    }

    @Test
    @DisplayName("updateAuthentication 매서드: 수정 전 이미지 파일 존재, 수정 이미지 파일 없는 경우 성공")
    void updateAuthentication_withBeforeMultipartFile_withoutAfterMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData(textData);
        MultipartFile photoData = null;
        String frequency = "주3회";
        String status = "STARTED";
        String photoUrl = "photoUrl.jpg";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, photoUrl, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        Optional<AuthenticationDocument> authenticationDocumentList = prepareNotListOneAuthentication(now, photoUrl, textData);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.updateAuthentication(authenticationUpdateRequest, photoData, missionId);

        // then
        verify(timeProvider).getCurrentDateTime();
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService).deleteFile(anyString());
        verify(fileService, never()).uploadFile(any(MultipartFile.class), anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).save(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(1);
//        AuthenticationDocument authentication = authenticationList.get(0);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
//        Assertions.assertThat(authentication.getPhotoData()).isNull();
    }

    @Test
    @DisplayName("updateAuthentication 매서드: 수정 전 이미지 파일 없음, 수정 이미지 파일 존재 성공")
    void updateAuthentication_withoutBeforeMultipartFile_withAfterMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData(textData);
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );
        String fileLocation = "test/photo.jpg";
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, null, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        Optional<AuthenticationDocument> authenticationDocumentList = prepareNotListOneAuthentication(now, null, textData);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);
        when(fileService.uploadFile(photoData, AUTHENTICATION_DIR)).thenReturn(fileLocation);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.updateAuthentication(authenticationUpdateRequest, photoData, missionId);

        // then
        verify(timeProvider).getCurrentDateTime();
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService, never()).deleteFile(anyString());
        verify(fileService).uploadFile(any(MultipartFile.class), anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).save(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(1);
//        AuthenticationDocument authentication = authenticationList.get(0);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
//        Assertions.assertThat(authentication.getPhotoData()).isEqualTo(fileLocation);
    }

    @Test
    @DisplayName("updateAuthentication 매서드: 수정 전 이미지 파일 없음, 수정 이미지 파일 없음 성공")
    void updateAuthentication_withoutBeforeMultipartFile_withoutAfterMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, null, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        Optional<AuthenticationDocument> authenticationDocumentList = prepareNotListOneAuthentication(now, null, textData);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.updateAuthentication(authenticationUpdateRequest, null, missionId);

        // then
        verify(timeProvider).getCurrentDateTime();
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService, never()).deleteFile(anyString());
        verify(fileService, never()).uploadFile(any(MultipartFile.class), anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).save(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(1);
//        AuthenticationDocument authentication = authenticationList.get(0);
//        Assertions.assertThat(authentication.getTextData()).isEqualTo(textData);
//        Assertions.assertThat(authentication.getPhotoData()).isNull();
    }

    @Test
    @DisplayName("updateAuthentication 매서드: 인증글을 작성한 적이 없어서 실패")
    void updateAuthentication_AuthenticationIsEmpty_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareParticipantDocument(missionId, "test", "test@example.com");

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Optional.empty());


        // when, then
        assertThrows(NotFoundException.class, () -> authenticationService.updateAuthentication(authenticationUpdateRequest, null, missionId));
    }

    @Test
    @DisplayName("updateAuthentication 매서드: 당일 인증글을 작성하지 않아서 실패")
    void updateAuthentication_AuthenticationNotFound_Failure() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String textData = "Test Authentication";
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData(textData);
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now.minusDays(1), null, textData);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Optional.empty());


        // when, then
        assertThrows(NotFoundException.class, () -> authenticationService.updateAuthentication(authenticationUpdateRequest, null, missionId));
    }

    @Test
    @DisplayName("deleteAuthentication 매서드: 기존 이미지 파일이 존재하는 인증글 삭제 성공")
    void deleteAuthentication_withMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String frequency = "주3회";
        String status = "STARTED";
        String photoUrl = "photoUrl.jpg";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, photoUrl, null);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        Optional<AuthenticationDocument> authenticationDocumentList = prepareNotListOneAuthentication(now.minusDays(1), photoUrl, null);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.deleteAuthentication(missionId);

        // then
        verify(timeProvider).getCurrentDateTime();
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService).deleteFile(anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).delete(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteAuthentication 매서드: 기존 이미지 파일이 없는 인증글 삭제 성공")
    void deleteAuthentication_withoutMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId = "65ea0c8007b2c737d6227bf4";
        String frequency = "주3회";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, frequency, status);
//        ParticipantDocument participantDocument = prepareOneAuthentication(prepareTwoAuthentication(prepareParticipantDocument(missionId, "test", "test@example.com"), now, null, null), now, null, null);

        ParticipantDocument participantDocument = prepareParticipantDocument(participantId, missionId, "test", "test@example.com");


        Optional<AuthenticationDocument> authenticationDocumentList = prepareNotListOneAuthentication(now, null, null);


        when(timeProvider.getCurrentDateTime()).thenReturn(now);
        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument);

        when(authenticationRepository.findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(authenticationDocumentList);


        // when
        authenticationService.deleteAuthentication(missionId);

        // then
        verify(timeProvider).getCurrentDateTime();
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
        verify(fileService, never()).deleteFile(anyString());
//        verify(participantRepository).save(any(ParticipantDocument.class));

        verify(authenticationRepository).findByParticipantIdAndMissionIdAndDate(any(ObjectId.class), any(ObjectId.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(authenticationRepository).delete(any(AuthenticationDocument.class));


//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("authenticationList 매서드: 5개 이하의 인증글, 첫 인증글 목록 성공")
    void authenticationList_5BelowAuthentication_First_Success() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId1 = "65ea0c8007b2c737d6227bf4";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, null, status);
//        ParticipantDocument participantDocument1 = prepareParticipantDocument(missionId, "test", "test@example.com");
//        prepareTwoAuthentication(participantDocument1, now, null, null);
//
//        ParticipantDocument participantDocument2 = prepareParticipantDocument(missionId, "test2", "test2@example.com");
//        prepareTwoAuthentication(participantDocument2, now.minusDays(3), null, null);
//
//        List<ParticipantDocument> participantDocumentList = new ArrayList<>();
//        participantDocumentList.add(participantDocument1);
//        participantDocumentList.add(participantDocument2);

        ParticipantDocument participantDocument1 = prepareParticipantDocument(participantId1, missionId, "test", "test@example.com");

        Page<AuthenticationDocument> authenticationListPage = prepareNAuthentication(4, now, null, null);


        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument1);
//        when(participantRepository.findByMissionId(any(ObjectId.class))).thenReturn(participantDocumentList);

        when(authenticationRepository.findAll(any(Pageable.class))).thenReturn(authenticationListPage);

        // when
        AuthenticationListResponse response = authenticationService.authenticationList(missionId, 0);

        // then
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
//        verify(participantRepository).findByMissionId(any(ObjectId.class));

        verify(authenticationRepository).findAll(any(Pageable.class));

        Assertions.assertThat(response).isNotNull();
//        List<Map<String, Object>> authenticationList = response.getAuthenticationData();
//
//        Assertions.assertThat(authenticationList.size()).isEqualTo(4);
//
//        Assertions.assertThat(authenticationList.get(0).get("username")).isEqualTo("test");
//        Assertions.assertThat(authenticationList.get(0).get("date")).isEqualTo(now.minusDays(1));
//        Assertions.assertThat(authenticationList.get(2).get("username")).isEqualTo("test2");
//        Assertions.assertThat(authenticationList.get(2).get("date")).isEqualTo(now.minusDays(4));

        List<AuthenticationList> authenticationList = response.getAuthenticationData();

        Assertions.assertThat(authenticationList.size()).isEqualTo(4);

        Assertions.assertThat(authenticationList.get(0).getDate()).isEqualTo(now.minusDays(1));
        Assertions.assertThat(authenticationList.get(2).getDate()).isEqualTo(now.minusDays(3));
    }

    @Test
    @DisplayName("authenticationList 매서드: 5개 초과의 인증글, 첫 인증글 성공")
    void authenticationList_5OverAuthentication_First_Success() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        int num = 0;
        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId1 = "65ea0c8007b2c737d6227bf4";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, null, status);
//        ParticipantDocument participantDocument1 = prepareParticipantDocument(missionId, "test", "test@example.com");
//        prepareTwoAuthentication(participantDocument1, now, null, null);
//
//        ParticipantDocument participantDocument2 = prepareParticipantDocument(missionId, "test2", "test2@example.com");
//        prepareTwoAuthentication(participantDocument2, now.minusDays(3), null, null);
//        prepareTwoAuthentication(participantDocument2, now.minusDays(5), null, null);

//        List<ParticipantDocument> participantDocumentList = new ArrayList<>();
//        participantDocumentList.add(participantDocument1);
//        participantDocumentList.add(participantDocument2);

        ParticipantDocument participantDocument1 = prepareParticipantDocument(participantId1, missionId, "test", "test@example.com");

        Page<AuthenticationDocument> authenticationDocumentPage = prepareNAuthentication(5, now, null, null);

        Pageable pageable = PageRequest.of(num, 5, Sort.by(Sort.Direction.DESC, "date"));


        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument1);
//        when(participantRepository.findByMissionId(any(ObjectId.class))).thenReturn(participantDocumentList);

        when(authenticationRepository.findAll(pageable)).thenReturn(authenticationDocumentPage);


        // when
        AuthenticationListResponse response = authenticationService.authenticationList(missionId, 0);

        // then
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
//        verify(participantRepository).findByMissionId(any(ObjectId.class));

        verify(authenticationRepository).findAll(any(Pageable.class));


        Assertions.assertThat(response).isNotNull();
//        List<Map<String, Object>> authenticationList = response.getAuthenticationData();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(5);
//
//        Assertions.assertThat(authenticationList.get(0).get("username")).isEqualTo("test");
//        Assertions.assertThat(authenticationList.get(0).get("date")).isEqualTo(now.minusDays(1));
//        Assertions.assertThat(authenticationList.get(2).get("username")).isEqualTo("test2");
//        Assertions.assertThat(authenticationList.get(2).get("date")).isEqualTo(now.minusDays(4));
//        Assertions.assertThat(authenticationList.get(4).get("username")).isEqualTo("test2");
//        Assertions.assertThat(authenticationList.get(4).get("date")).isEqualTo(now.minusDays(6));

        List<AuthenticationList> authenticationList = response.getAuthenticationData();
        Assertions.assertThat(authenticationList.size()).isEqualTo(5);

        Assertions.assertThat(authenticationList.get(0).getDate()).isEqualTo(now.minusDays(1));
        Assertions.assertThat(authenticationList.get(2).getDate()).isEqualTo(now.minusDays(3));
        Assertions.assertThat(authenticationList.get(4).getDate()).isEqualTo(now.minusDays(5));
    }

    @Test
    @DisplayName("authenticationList 매서드: 5개 초과의 인증글, 두번째 인증글 성공")
    void authenticationList_5OverAuthentication_Second_Success() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        int num = 1;
        String missionId = "65ea0c8007b2c737d6227bf0";
        String participantId1 = "65ea0c8007b2c737d6227bf4";
        String status = "STARTED";

        MissionDocument missionDocument = prepareMissionDocument(missionId, null, status);
//        ParticipantDocument participantDocument1 = prepareParticipantDocument(missionId, "test", "test@example.com");
//        prepareTwoAuthentication(participantDocument1, now, null, null);
//
//        ParticipantDocument participantDocument2 = prepareParticipantDocument(missionId, "test2", "test2@example.com");
//        prepareTwoAuthentication(participantDocument2, now.minusDays(3), null, null);
//        prepareTwoAuthentication(participantDocument2, now.minusDays(5), null, null);
//
//        List<ParticipantDocument> participantDocumentList = new ArrayList<>();
//        participantDocumentList.add(participantDocument1);
//        participantDocumentList.add(participantDocument2);

        ParticipantDocument participantDocument1 = prepareParticipantDocument(participantId1, missionId, "test", "test@example.com");

        Page<AuthenticationDocument> authenticationDocumentPage = prepareNAuthentication(1, now, null, null);

        Pageable pageable = PageRequest.of(num, 5, Sort.by(Sort.Direction.DESC, "date"));


        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(userService.getParticipantDocument(any(MissionDocument.class), anyString())).thenReturn(participantDocument1);
//        when(participantRepository.findByMissionId(any(ObjectId.class))).thenReturn(participantDocumentList);

        when(authenticationRepository.findAll(pageable)).thenReturn(authenticationDocumentPage);


        // when
        AuthenticationListResponse response = authenticationService.authenticationList(missionId, num);

        // then
        verify(missionService).getMissionDocument(anyString());
        verify(userService).getParticipantDocument(any(MissionDocument.class), anyString());
//        verify(participantRepository).findByMissionId(any(ObjectId.class));

        verify(authenticationRepository).findAll(any(Pageable.class));


        Assertions.assertThat(response).isNotNull();
//        List<Map<String, Object>> authenticationList = response.getAuthenticationData();
//        Assertions.assertThat(authenticationList.size()).isEqualTo(1);
//
//        Assertions.assertThat(authenticationList.get(0).get("username")).isEqualTo("test2");
//        Assertions.assertThat(authenticationList.get(0).get("date")).isEqualTo(now.minusDays(7));

        List<AuthenticationList> authenticationList = response.getAuthenticationData();
        Assertions.assertThat(authenticationList.size()).isEqualTo(1);

        Assertions.assertThat(authenticationList.get(0).getDate()).isEqualTo(now.minusDays(1));
    }

    // Mock SecurityContextHolder 설정
    private void prepareSecurityContextHolder() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private CustomOAuth2User prepareCustomOAuth2User() {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(User.builder()
                .email("test@example.com")
                .role("ROLE_USER")
                .name("test")
                .build());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customOAuth2User);
        return customOAuth2User;
    }

    private MissionDocument prepareMissionDocument(String missionId, String frequency, String status) {
        return MissionDocument.builder()
                .id(new ObjectId(missionId))
                .frequency(frequency)
                .status(status)
                .build();
    }

    private ParticipantDocument prepareParticipantDocument(String participantId, String missionId, String username, String userEmail) {
        return ParticipantDocument.builder()
                .id(new ObjectId(participantId))
                .missionId(new ObjectId(missionId))
                .userEmail(userEmail)
                .username(username)
//                .authentication(new ArrayList<>())
                .build();
    }

//    private ParticipantDocument prepareTwoAuthentication(ParticipantDocument participantDocument, LocalDateTime now, String photoUrl, String textData) {
//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//
//        for(int i = 0; i < 2; i++) {
//            authenticationList.add(AuthenticationDocument.builder()
//                    .date(now.minusDays(i + 1))
//                    .photoData(photoUrl)
//                    .textData(textData)
//                    .build());
//        }
//
//        participantDocument.setAuthentication(authenticationList);
//
//        return participantDocument;
//    }

    private List<AuthenticationDocument> prepareTwoAuthentication(LocalDateTime now, String photoUrl, String textData) {
        List<AuthenticationDocument> authenticationDocumentList = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            AuthenticationDocument authenticationDocument = AuthenticationDocument.builder()
                    .date(now.minusDays(i + 1))
                    .photoData(photoUrl)
                    .textData(textData)
                    .build();

            authenticationDocumentList.add(authenticationDocument);
        }

        return authenticationDocumentList;
    }

//    private ParticipantDocument prepareOneAuthentication(ParticipantDocument participantDocument, LocalDateTime now, String photoUrl, String textData) {
//        List<AuthenticationDocument> authenticationList = participantDocument.getAuthentication();
//
//        authenticationList.add(AuthenticationDocument.builder()
//                .date(now)
//                .photoData(photoUrl)
//                .textData(textData)
//                .build());
//
//        participantDocument.setAuthentication(authenticationList);
//
//        return participantDocument;
//    }

//    private List<AuthenticationDocument> prepareOneAuthentication(LocalDateTime now, String photoUrl, String textData) {
//        List<AuthenticationDocument> authenticationDocumentList = null;
//
//        AuthenticationDocument authenticationDocument = AuthenticationDocument.builder()
//                .date(now)
//                .photoData(photoUrl)
//                .textData(textData)
//                .build();
//
//        authenticationDocumentList.add(authenticationDocument);
//
//        return authenticationDocumentList;
//    }

    private Optional<AuthenticationDocument> prepareNotListOneAuthentication(LocalDateTime now, String photoUrl, String textData) {
        AuthenticationDocument authenticationDocument = AuthenticationDocument.builder()
                .date(now)
                .photoData(photoUrl)
                .textData(textData)
                .build();

        return Optional.of(authenticationDocument);
    }

    private Page<AuthenticationDocument> prepareNAuthentication(int num, LocalDateTime now, String photoUrl, String textData) {
        List<AuthenticationDocument> authenticationDocumentList = new ArrayList<>();

        for(int i = 0; i < num; i++) {
            AuthenticationDocument authenticationDocument = AuthenticationDocument.builder()
                    .completed(true)
                    .username("test")
                    .userEmail("test@example.com")
                    .date(now.minusDays(i + 1))
                    .photoData(photoUrl)
                    .textData(textData)
                    .build();

            authenticationDocumentList.add(authenticationDocument);
        }
//        Page<AuthenticationDocument> authenticationDocumentPage = (Page<AuthenticationDocument>) authenticationDocumentList;

        return new PageImpl<>(authenticationDocumentList);
    }
}
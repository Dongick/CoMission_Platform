package mission.service;

import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.User;
import mission.exception.BadRequestException;
import mission.exception.ConflictException;
import mission.exception.ForbiddenException;
import mission.exception.NotFoundException;
import mission.repository.MissionRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {
    @Mock
    private MissionRepository missionRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private AWSS3Service awss3Service;
    @Mock
    private FileService fileService;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private ParticipantService participantService;
    @InjectMocks
    private MissionService missionService;
    private static String MISSION_DIR = "missions/";

    @Test
    @DisplayName("createMission 매서드: 이미지 파일을 가진 미션 생성 성공")
    void createMission_withMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        MissionCreateRequest missionCreateRequest = new MissionCreateRequest();
        missionCreateRequest.setTitle("Test Mission");
        missionCreateRequest.setDescription("This is a test mission");
        missionCreateRequest.setMinParticipants(3);
        missionCreateRequest.setDuration(7);
        missionCreateRequest.setFrequency("daily");
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );

        String fileLocation = "test/photo.jpg";

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(fileService.uploadFile(photoData, MISSION_DIR)).thenReturn(fileLocation);
        when(missionRepository.save(any())).thenReturn(MissionDocument.builder()
                .id(new ObjectId())
                .build());

        // when
        missionService.createMission(missionCreateRequest, photoData);

        // then
        verify(fileService).uploadFile(any(MultipartFile.class), anyString());
        verify(missionRepository).save(any(MissionDocument.class));
        verify(participantService).saveParticipant(any(ObjectId.class), any(LocalDateTime.class), anyString(), anyString());
    }

    @Test
    @DisplayName("createMission 매서드: 이미지 파일이 없는 미션 생성 성공")
    void createMission_withoutMultipartFile_Success() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        MissionCreateRequest missionCreateRequest = new MissionCreateRequest();
        missionCreateRequest.setTitle("Test Mission");
        missionCreateRequest.setDescription("This is a test mission");
        missionCreateRequest.setMinParticipants(3);
        missionCreateRequest.setDuration(7);
        missionCreateRequest.setFrequency("daily");
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.save(any())).thenReturn(MissionDocument.builder()
                .id(new ObjectId())
                .build());

        // when
        missionService.createMission(missionCreateRequest, photoData);

        // then
        verify(missionRepository).save(any(MissionDocument.class));
        verify(participantService).saveParticipant(any(ObjectId.class), any(LocalDateTime.class), anyString(), anyString());
    }

    @Test
    @DisplayName("createMission 매서드: 이미지 파일 저장 실패")
    void createMission_Failure() throws IOException {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        MissionCreateRequest missionCreateRequest = new MissionCreateRequest();
        missionCreateRequest.setTitle("Test Mission");
        missionCreateRequest.setDescription("This is a test mission");
        missionCreateRequest.setMinParticipants(3);
        missionCreateRequest.setDuration(7);
        missionCreateRequest.setFrequency("daily");
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(fileService.uploadFile(photoData, MISSION_DIR)).thenThrow(IOException.class);

        // when, then
        assertThrows(IOException.class, () -> missionService.createMission(missionCreateRequest, photoData));
    }

    @Test
    @DisplayName("updateMission 매서드: 수정 전 이미지 파일 존재, 수정 이미지 파일 존재, 현재 미션 인원수가 수정된 최소 인원수보다 적은 경우 성공")
    void updateMission_withBeforeMultipartFile_withAfterMultipartFile_NotChangeStatus_Success() throws IOException{
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String photoUrl = "photoUrl.jpg";
        String status = "CREATED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );

        String fileLocation = "test/photo.jpg";

        MissionDocument missionDocument = prepareMissionDocument(title, description, minParticipants, participants, duration, frequency, status, userEmail, photoUrl);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));
        when(fileService.uploadFile(photoData, MISSION_DIR)).thenReturn(fileLocation);

        // when
        missionService.updateMission(missionUpdateRequest, photoData, missionId);

        // then
        Assertions.assertThat(missionUpdateRequest.getAfterTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionUpdateRequest.getMinParticipants()).isEqualTo(missionDocument.getMinParticipants());
        Assertions.assertThat(missionUpdateRequest.getDescription()).isEqualTo(missionDocument.getDescription());
        Assertions.assertThat(missionUpdateRequest.getFrequency()).isEqualTo(missionDocument.getFrequency());
        Assertions.assertThat(missionUpdateRequest.getDuration()).isEqualTo(missionDocument.getDuration());
        Assertions.assertThat(missionDocument.getStatus()).isEqualTo(status);
        Assertions.assertThat(missionDocument.getDeadline()).isNull();
        Assertions.assertThat(missionDocument.getStartDate()).isNull();

        verify(missionRepository).save(missionDocument);
        verify(fileService).deleteFile(anyString());
        verify(fileService).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("updateMission 매서드: 수정 전 이미지 파일 존재, 수정 이미지 파일 존재, 현재 미션 인원수가 수정된 최소 인원수보다 같거나 많은 경우 성공")
    void updateMission_withBeforeMultipartFile_withAfterMultipartFile_ChangeStatus_Success() throws IOException{
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String photoUrl = "photoUrl.jpg";
        String status = "CREATED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants - 1);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );

        String fileLocation = "test/photo.jpg";

        MissionDocument missionDocument = prepareMissionDocument(title, description, minParticipants, participants, duration, frequency, status, userEmail, photoUrl);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));
        when(fileService.uploadFile(photoData, MISSION_DIR)).thenReturn(fileLocation);

        // when
        missionService.updateMission(missionUpdateRequest, photoData, missionId);

        // then
        Assertions.assertThat(missionUpdateRequest.getAfterTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionUpdateRequest.getMinParticipants()).isEqualTo(missionDocument.getMinParticipants());
        Assertions.assertThat(missionUpdateRequest.getDescription()).isEqualTo(missionDocument.getDescription());
        Assertions.assertThat(missionUpdateRequest.getFrequency()).isEqualTo(missionDocument.getFrequency());
        Assertions.assertThat(missionUpdateRequest.getDuration()).isEqualTo(missionDocument.getDuration());
        Assertions.assertThat(missionDocument.getStatus()).isEqualTo("STARTED");
        Assertions.assertThat(missionDocument.getDeadline()).isNotNull();
        Assertions.assertThat(missionDocument.getStartDate()).isNotNull();

        verify(missionRepository).save(missionDocument);
        verify(fileService).deleteFile(anyString());
        verify(fileService).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("updateMission 매서드: 수정 전 이미지 파일 존재, 수정 이미지 파일 없는 경우 성공")
    void updateMission_withBeforeMultipartFile_withoutAfterMultipartFile_Success() throws IOException{
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String photoUrl = "photoUrl.jpg";
        String status = "CREATED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = null;

        MissionDocument missionDocument = prepareMissionDocument(title, description, minParticipants, participants, duration, frequency, status, userEmail, photoUrl);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));

        // when
        missionService.updateMission(missionUpdateRequest, photoData, missionId);

        // then
        Assertions.assertThat(missionUpdateRequest.getAfterTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionUpdateRequest.getMinParticipants()).isEqualTo(missionDocument.getMinParticipants());
        Assertions.assertThat(missionUpdateRequest.getDescription()).isEqualTo(missionDocument.getDescription());
        Assertions.assertThat(missionUpdateRequest.getFrequency()).isEqualTo(missionDocument.getFrequency());
        Assertions.assertThat(missionUpdateRequest.getDuration()).isEqualTo(missionDocument.getDuration());
        Assertions.assertThat(missionDocument.getStatus()).isEqualTo(status);
        Assertions.assertThat(missionDocument.getDeadline()).isNull();
        Assertions.assertThat(missionDocument.getStartDate()).isNull();

        verify(missionRepository).save(missionDocument);
        verify(fileService).deleteFile(anyString());
        verify(fileService, never()).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("updateMission 매서드: 수정 전 이미지 파일 없음, 수정 이미지 파일 존재하는 경우 성공")
    void updateMission_withoutBeforeMultipartFile_withAfterMultipartFile_Success() throws IOException{
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "CREATED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = new MockMultipartFile("photoData", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );

        String fileLocation = "test/photo.jpg";

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));
        when(fileService.uploadFile(photoData, MISSION_DIR)).thenReturn(fileLocation);

        // when
        missionService.updateMission(missionUpdateRequest, photoData, missionId);

        // then
        Assertions.assertThat(missionUpdateRequest.getAfterTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionUpdateRequest.getMinParticipants()).isEqualTo(missionDocument.getMinParticipants());
        Assertions.assertThat(missionUpdateRequest.getDescription()).isEqualTo(missionDocument.getDescription());
        Assertions.assertThat(missionUpdateRequest.getFrequency()).isEqualTo(missionDocument.getFrequency());
        Assertions.assertThat(missionUpdateRequest.getDuration()).isEqualTo(missionDocument.getDuration());
        Assertions.assertThat(missionDocument.getStatus()).isEqualTo(status);
        Assertions.assertThat(missionDocument.getDeadline()).isNull();
        Assertions.assertThat(missionDocument.getStartDate()).isNull();

        verify(missionRepository).save(missionDocument);
        verify(fileService, never()).deleteFile(anyString());
        verify(fileService).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("updateMission 매서드: 수정 전 이미지 파일 없음, 수정 이미지 파일 없는 경우 성공")
    void updateMission_withoutBeforeMultipartFile_withoutAfterMultipartFile_Success() throws IOException{
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "CREATED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = null;

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));

        // when
        missionService.updateMission(missionUpdateRequest, photoData, missionId);

        // then
        Assertions.assertThat(missionUpdateRequest.getAfterTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionUpdateRequest.getMinParticipants()).isEqualTo(missionDocument.getMinParticipants());
        Assertions.assertThat(missionUpdateRequest.getDescription()).isEqualTo(missionDocument.getDescription());
        Assertions.assertThat(missionUpdateRequest.getFrequency()).isEqualTo(missionDocument.getFrequency());
        Assertions.assertThat(missionUpdateRequest.getDuration()).isEqualTo(missionDocument.getDuration());
        Assertions.assertThat(missionDocument.getStatus()).isEqualTo(status);
        Assertions.assertThat(missionDocument.getDeadline()).isNull();
        Assertions.assertThat(missionDocument.getStartDate()).isNull();

        verify(missionRepository).save(missionDocument);
        verify(fileService, never()).deleteFile(anyString());
        verify(fileService, never()).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("updateMission 매서드: 수정하려는 미션이 존재하지 않아 실패")
    void updateMission_MissionNotFound_Failure(){
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int duration = 10;
        String frequency = "weekly";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = null;

        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> missionService.updateMission(missionUpdateRequest, photoData, missionId));
    }

    @Test
    @DisplayName("updateMission 매서드: 수정하려는 미션이 이미 시작된 경우 실패")
    void updateMission_MissionAlreadyStarted_Failure(){
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "STARTED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = null;

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));

        // when, then
        assertThrows(ConflictException.class, () -> missionService.updateMission(missionUpdateRequest, photoData, missionId));
    }

    @Test
    @DisplayName("updateMission 매서드: 수정하려는 미션이 이미 종료된 경우 실패")
    void updateMission_MissionAlreadyCompleted_Failure(){
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "COMPLETED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = null;

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));

        // when, then
        assertThrows(BadRequestException.class, () -> missionService.updateMission(missionUpdateRequest, photoData, missionId));
    }

    @Test
    @DisplayName("updateMission 매서드: 해당 미션을 생성한 사용자와 수정하려는 사용자가 다른 경우 실패")
    void updateMission_DifferentUser_Failure(){
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = "different test email";
        String missionId = "missionId";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "CREATED";

        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle(title);
        missionUpdateRequest.setDescription(description);
        missionUpdateRequest.setMinParticipants(minParticipants);
        missionUpdateRequest.setDuration(duration);
        missionUpdateRequest.setFrequency(frequency);
        MultipartFile photoData = null;

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));

        // when, then
        assertThrows(ForbiddenException.class, () -> missionService.updateMission(missionUpdateRequest, photoData, missionId));
    }

    @Test
    @DisplayName("missionInfo 매서드: 로그인을 하지 않은 경우 성공")
    void missionInfo_withoutCustomOAuth2User_Success() {
        // given
        prepareSecurityContextHolder();
        String missionId = "65ea0c8007b2c737d6227bf0";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "CREATED";
        String userEmail = "test@example.com";

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(missionId), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));

        // when
        MissionInfoResponse missionInfoResponse = missionService.missionInfo(missionId);

        // then
        Assertions.assertThat(missionInfoResponse.getTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionInfoResponse.getParticipant()).isFalse();
    }

    @Test
    @DisplayName("missionInfo 매서드: 로그인한 사용자가 해당 미션에 참여한 경우 성공")
    void missionInfo_withCustomOAuth2User_YesParticipant_Success() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();
        String username = customOAuth2User.getName();

        String missionId = "65ea0c8007b2c737d6227bf0";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "CREATED";

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(missionId), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        ParticipantDocument participantDocument = ParticipantDocument.builder()
                .userEmail(userEmail)
                .missionId(new ObjectId(missionId))
                .joinedAt(LocalDateTime.now())
//                .authentication(new ArrayList<>())
                .username(username)
                .build();

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));
        when(participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(),userEmail)).thenReturn(Optional.of(participantDocument));

        // when
        MissionInfoResponse missionInfoResponse = missionService.missionInfo(missionId);

        // then
        Assertions.assertThat(missionInfoResponse.getTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionInfoResponse.getParticipant()).isTrue();
    }

    @Test
    @DisplayName("missionInfo 매서드: 로그인한 사용자가 해당 미션에 참여하지 않은 경우 성공")
    void missionInfo_withCustomOAuth2User_NoParticipant_Success() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User();

        String userEmail = customOAuth2User.getEmail();

        String missionId = "65ea0c8007b2c737d6227bf0";

        String title = "Updated Test Mission";
        String description = "This is an updated test mission";
        int minParticipants = 5;
        int participants = 4;
        int duration = 10;
        String frequency = "weekly";
        String status = "CREATED";

        MissionDocument missionDocument = prepareMissionDocument(new ObjectId(missionId), title, description, minParticipants, participants, duration, frequency, status, userEmail);

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(missionDocument));
        when(participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(),userEmail)).thenReturn(Optional.empty());

        // when
        MissionInfoResponse missionInfoResponse = missionService.missionInfo(missionId);

        // then
        Assertions.assertThat(missionInfoResponse.getTitle()).isEqualTo(missionDocument.getTitle());
        Assertions.assertThat(missionInfoResponse.getParticipant()).isFalse();
    }

    @Test
    @DisplayName("missionInfo 매서드: 정보를 확인하려는 미션이 존재하지 않아 실패")
    void missionInfo_MissionNotFound_Failure(){
        // given
        String missionId = "missionId";

        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> missionService.missionInfo(missionId));
    }

    @Test
    @DisplayName("missionSearch 매서드: 원하는 미션 search 성공")
    void missionSearch() {
        String title = "test";

        MissionSearchRequest missionSearchRequest = new MissionSearchRequest();
        missionSearchRequest.setTitle(title);

        MissionInfo missionInfo1 = new MissionInfo();
        MissionInfo missionInfo2 = new MissionInfo();
        List<MissionInfo> missionInfoList = Arrays.asList(missionInfo1, missionInfo2);

        when(missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(missionSearchRequest.getTitle())).thenReturn(missionInfoList);

        // when
        MissionSearchResponse missionSearchResponse = missionService.missionSearch(missionSearchRequest);

        Assertions.assertThat(missionSearchResponse).isNotNull();
        Assertions.assertThat(missionSearchResponse.getMissionInfoList()).isEqualTo(missionInfoList);
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

    private MissionDocument prepareMissionDocument(String title, String description, int minParticipants, int participants, int duration, String frequency, String status, String userEmail, String photoUrl) {
        return MissionDocument.builder()
                .title(title)
                .description(description)
                .minParticipants(minParticipants)
                .participants(participants)
                .duration(duration)
                .frequency(frequency)
                .status(status)
                .creatorEmail(userEmail)
                .photoUrl(photoUrl)
                .build();
    }

    private MissionDocument prepareMissionDocument(ObjectId id, String title, String description, int minParticipants, int participants, int duration, String frequency, String status, String userEmail) {
        return MissionDocument.builder()
                .id(id)
                .title(title)
                .description(description)
                .minParticipants(minParticipants)
                .participants(participants)
                .duration(duration)
                .frequency(frequency)
                .status(status)
                .creatorEmail(userEmail)
                .build();
    }
}
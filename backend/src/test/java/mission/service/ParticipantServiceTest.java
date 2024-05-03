package mission.service;

import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantRequest;
import mission.dto.user.User;
import mission.enums.MissionStatus;
import mission.exception.BadRequestException;
import mission.exception.ConflictException;
import mission.exception.NotFoundException;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import mission.util.TimeProvider;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MissionRepository missionRepository;
    @Mock
    private TimeProvider timeProvider;
    @InjectMocks
    private ParticipantService participantService;

    @BeforeEach
    void setUp() {
        // Mock SecurityContextHolder 설정
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(User.builder()
                .email("test@example.com")
                .role("ROLE_USER")
                .name("test")
                .build());
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(customOAuth2User);
    }

    @Test
    @DisplayName("participateMission 매서드: 미션 참여에 성공했을 때 해당 미션의 최소 인원수를 충족하여 미션의 상태가 STARTED로 변환")
    void participateMission_ChangeStatus_Success() {
        // given
        String userEmail = "test@example.com";

        ParticipantRequest participantRequest = new ParticipantRequest();
        ObjectId missionId = new ObjectId();
        participantRequest.setId(missionId.toString());

        MissionDocument missionDocument = MissionDocument.builder()
                .id(missionId)
                .status(MissionStatus.CREATED.name())
                .minParticipants(3)
                .participants(2)
                .build();

        when(timeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(missionRepository.findById(participantRequest.getId())).thenReturn(Optional.of(missionDocument));
        when(participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail)).thenReturn(Optional.empty());

        // when
        participantService.participateMission(participantRequest);

        // then
        Assertions.assertThat(3).isEqualTo(missionDocument.getParticipants());
        Assertions.assertThat(MissionStatus.STARTED.name()).isEqualTo(missionDocument.getStatus());

        verify(participantRepository).save(any(ParticipantDocument.class));
        verify(missionRepository).save(missionDocument);
        verify(missionRepository).findById(participantRequest.getId());
        verify(participantRepository).findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);
    }

    @Test
    @DisplayName("participateMission 매서드: 미션 참여에 성공했을 때 해당 미션의 최소 인원수를 충족하지 못하여 미션의 상태가 그대로 CREATED")
    void participateMission_NotChangeStatus_Success() {
        // given
        String userEmail = "test@example.com";

        ParticipantRequest participantRequest = new ParticipantRequest();
        ObjectId missionId = new ObjectId();
        participantRequest.setId(missionId.toString());

        MissionDocument missionDocument = MissionDocument.builder()
                .id(missionId)
                .status(MissionStatus.CREATED.name())
                .minParticipants(3)
                .participants(1)
                .build();

        when(missionRepository.findById(participantRequest.getId())).thenReturn(Optional.of(missionDocument));
        when(participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail)).thenReturn(Optional.empty());

        // when
        participantService.participateMission(participantRequest);

        // then
        Assertions.assertThat(2).isEqualTo(missionDocument.getParticipants());
        Assertions.assertThat(MissionStatus.CREATED.name()).isEqualTo(missionDocument.getStatus());

        verify(participantRepository).save(any(ParticipantDocument.class));
        verify(missionRepository).save(missionDocument);
        verify(missionRepository).findById(participantRequest.getId());
        verify(participantRepository).findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);
    }

    @Test
    @DisplayName("participateMission 매서드: 참여하려는 미션이 존재하지 않아 실패")
    void participateMission_MissionNotFound() {
        // given
        ParticipantRequest participantRequest = new ParticipantRequest();
        ObjectId missionId = new ObjectId();
        participantRequest.setId(missionId.toString());

        when(missionRepository.findById(participantRequest.getId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> participantService.participateMission(participantRequest));
    }

    @Test
    @DisplayName("participateMission 매서드: 참여하려는 미션이 이미 완료되어 실패")
    void participateMission_MissionAlreadyCompleted() {
        // given
        ParticipantRequest participantRequest = new ParticipantRequest();
        ObjectId missionId = new ObjectId();
        participantRequest.setId(missionId.toString());

        MissionDocument missionDocument = MissionDocument.builder()
                .id(missionId)
                .status(MissionStatus.COMPLETED.name())
                .minParticipants(3)
                .participants(1)
                .build();

        when(missionRepository.findById(participantRequest.getId())).thenReturn(Optional.of(missionDocument));

        // when, then
        assertThrows(BadRequestException.class, () -> participantService.participateMission(participantRequest));
    }

    @Test
    @DisplayName("participateMission 매서드: 해당 미션에 이미 참여한 상태여서 실패")
    void participateMission_AlreadyParticipated() {
        // given
        String userEmail = "test@example.com";

        ParticipantRequest participantRequest = new ParticipantRequest();
        ObjectId missionId = new ObjectId();
        participantRequest.setId(missionId.toString());

        MissionDocument missionDocument = MissionDocument.builder()
                .id(missionId)
                .status(MissionStatus.CREATED.name())
                .minParticipants(3)
                .participants(1)
                .build();

        ParticipantDocument participantDocument = ParticipantDocument.builder().build();

        when(missionRepository.findById(participantRequest.getId())).thenReturn(Optional.of(missionDocument));
        when(participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail)).thenReturn(Optional.of(participantDocument));

        // when, then
        assertThrows(ConflictException.class, () -> participantService.participateMission(participantRequest));
    }
}
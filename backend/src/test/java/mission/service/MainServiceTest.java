package mission.service;

import mission.dto.User;
import mission.dto.main.MainLazyLoadingResponse;
import mission.dto.main.MainResponse;
import mission.dto.mission.MissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantMissionId;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainServiceTest {
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MissionRepository missionRepository;
    @InjectMocks
    private MainService mainService;

    @Test
    @DisplayName("getInitialMissionList 매서드: 로그인한 사용자가 참가한 미션이 존재할 때 성공")
    void getInitialMissionList_withCustomOAuth2User_withParticipantMission() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User("test@example.com");
        String email = customOAuth2User.getEmail();

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};
        List<ParticipantMissionId> participantMissionIdList = new ArrayList<>();
        for (ObjectId id : ids) {
            ParticipantMissionId participantMissionId = new ParticipantMissionId();
            participantMissionId.setMissionId(id);
            participantMissionIdList.add(participantMissionId);
        }

        List<MissionInfo> participantMissionInfoList = new ArrayList<>();
        List<MissionInfo> missionInfoList = new ArrayList<>();

        when(participantRepository.findByUserEmail(email)).thenReturn(participantMissionIdList);
        when(missionRepository.findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList())).thenReturn(participantMissionInfoList);
        when(missionRepository.findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(0, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getInitialMissionList();

        // then
        Assertions.assertThat(participantMissionInfoList).isEqualTo(mainResponse.getParticipantMissionInfoList());
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository).findByUserEmail(email);
        verify(missionRepository).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(participantMissionIdList.stream().map(ParticipantMissionId::getMissionId).toList());
        verify(missionRepository).findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("getInitialMissionList 매서드: 로그인한 사용자가 참가한 미션이 없을 때 성공")
    void getInitialMissionList_withCustomOAuth2User_withoutParticipantMission() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User("test@example.com");
        String email = customOAuth2User.getEmail();

        List<MissionInfo> missionInfoList = new ArrayList<>();

        when(participantRepository.findByUserEmail(email)).thenReturn(new ArrayList<>());
        when(missionRepository.findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(0, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getInitialMissionList();

        //then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository).findByUserEmail(email);
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("getInitialMissionList 매서드: 사용자가 로그인을 하지 않았을 때 성공")
    void getInitialMissionList_withoutCustomOAuth2User() {
        // given
        prepareSecurityContextHolder();

        List<MissionInfo> missionInfoList = new ArrayList<>();

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
        when(missionRepository.findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(0, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getInitialMissionList();

        // then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository, never()).findByUserEmail(anyString());
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("getLazyLoadingMissionList 매서드: 성공")
    void getLazyLoadingMissionList() {
        // given
        int num = 3;

        List<MissionInfo> missionInfoList = new ArrayList<>();

        when(missionRepository.findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainLazyLoadingResponse mainLazyLoadingResponse = mainService.getLazyLoadingMissionList(num);

        // then
        Assertions.assertThat(missionInfoList).isEqualTo(mainLazyLoadingResponse.getMissionInfoList());

        verify(missionRepository).findAllByStatusNotOrderByCreatedAtDesc(PageRequest.of(num, 20));
    }

    private CustomOAuth2User prepareCustomOAuth2User(String email) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(User.builder()
                .email(email)
                .role("ROLE_USER")
                .name("test")
                .build());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customOAuth2User);
        return customOAuth2User;
    }

    private void prepareSecurityContextHolder() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
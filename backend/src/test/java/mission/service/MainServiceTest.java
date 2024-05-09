package mission.service;


import mission.document.ParticipantDocument;
import mission.dto.main.MainResponse;
import mission.dto.mission.MissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.User;
import mission.exception.BadRequestException;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("getMainMissionList 매서드: 매서드 성공, sort == latest, filter == all")
    void getMainMissionList_findAllByStatusNotCompletedOrderByCreatedAtDesc() {
        // given
        prepareSecurityContextHolder();
        CustomOAuth2User customOAuth2User = prepareCustomOAuth2User("test@example.com");
        String email = customOAuth2User.getEmail();

        String sort = "latest";
        int num = 0;
        String filter = "all";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<ParticipantDocument> participantDocumentList = prepareParticipantDocumentList(ids, email);

        List<MissionInfo> participantMissionInfoList = prepareMissionInfoList(ids);

        List<MissionInfo> missionInfoList = prepareMissionInfoList(ids);

        when(participantRepository.findByUserEmail(email)).thenReturn(participantDocumentList);
        when(missionRepository.findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList())).thenReturn(participantMissionInfoList);
        when(missionRepository.findAllByStatusNotCompletedOrderByCreatedAtDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);

        // then
        Assertions.assertThat(participantMissionInfoList).isEqualTo(mainResponse.getParticipantMissionInfoList());
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository).findByUserEmail(email);
        verify(missionRepository).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(participantDocumentList.stream().map(ParticipantDocument::getMissionId).toList());
        verify(missionRepository).findAllByStatusNotCompletedOrderByCreatedAtDesc(PageRequest.of(num, 20));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 성공, sort == latest, filter == created")
    void getMainMissionList_findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc() {
        // given
        prepareSecurityContextHolder();

        String sort = "latest";
        int num = 0;
        String filter = "created";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<MissionInfo> missionInfoList = prepareMissionInfoList(ids);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
        when(missionRepository.findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);

        // then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository, never()).findByUserEmail(anyString());
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotCompletedAndStartedOrderByCreatedAtDesc(PageRequest.of(num, 20));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 성공, sort == latest, filter == started")
    void getMainMissionList_findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc() {
        // given
        prepareSecurityContextHolder();

        String sort = "latest";
        int num = 0;
        String filter = "started";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<MissionInfo> missionInfoList = prepareMissionInfoList(ids);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
        when(missionRepository.findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);

        // then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository, never()).findByUserEmail(anyString());
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotCompletedAndCreatedOrderByCreatedAtDesc(PageRequest.of(num, 20));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 성공, sort == participants, filter == all")
    void getMainMissionList_findAllByStatusNotCompletedOrderByParticipantsDesc() {
        // given
        prepareSecurityContextHolder();

        String sort = "participants";
        int num = 0;
        String filter = "all";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<MissionInfo> missionInfoList = prepareMissionInfoList(ids);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
        when(missionRepository.findAllByStatusNotCompletedOrderByParticipantsDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);

        // then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository, never()).findByUserEmail(anyString());
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotCompletedOrderByParticipantsDesc(PageRequest.of(num, 20));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 성공, sort == participants, filter == created")
    void getMainMissionList_findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc() {
        // given
        prepareSecurityContextHolder();

        String sort = "participants";
        int num = 0;
        String filter = "created";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<MissionInfo> missionInfoList = prepareMissionInfoList(ids);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
        when(missionRepository.findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);

        // then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository, never()).findByUserEmail(anyString());
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotCompletedAndStartedOrderByParticipantsDesc(PageRequest.of(num, 20));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 성공, sort == participants, filter == started")
    void getMainMissionList_findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc() {
        // given
        prepareSecurityContextHolder();

        String sort = "participants";
        int num = 0;
        String filter = "started";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<MissionInfo> missionInfoList = prepareMissionInfoList(ids);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
        when(missionRepository.findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc(PageRequest.of(num, 20))).thenReturn(missionInfoList);

        // when
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);

        // then
        Assertions.assertThat(mainResponse.getParticipantMissionInfoList()).isNull();
        Assertions.assertThat(missionInfoList).isEqualTo(mainResponse.getMissionInfoList());

        verify(participantRepository, never()).findByUserEmail(anyString());
        verify(missionRepository, never()).findByMissionIdInAndStatusNotOrderByCreatedAtDesc(anyList());
        verify(missionRepository).findAllByStatusNotCompletedAndCreatedOrderByParticipantsDesc(PageRequest.of(num, 20));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 실패, filter VALIDATION_FAILED")
    void getMainMissionList_filter_VALIDATION_FAILED() {
        // given
        prepareSecurityContextHolder();

        String sort = "participants";
        int num = 0;
        String filter = "test";

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);

        // when, then
        assertThrows(BadRequestException.class, () -> mainService.getMainMissionList(sort, num, filter));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 실패, sort VALIDATION_FAILED")
    void getMainMissionList_sort_VALIDATION_FAILED() {
        // given
        prepareSecurityContextHolder();

        String sort = "test";
        int num = 0;
        String filter = "all";

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);

        // when, then
        assertThrows(BadRequestException.class, () -> mainService.getMainMissionList(sort, num, filter));
    }

    @Test
    @DisplayName("getMainMissionList 매서드: 매서드 실패, num VALIDATION_FAILED")
    void getMainMissionList_num_VALIDATION_FAILED() {
        // given
        String sort = "latest";
        int num = -1;
        String filter = "all";

        // when, then
        assertThrows(BadRequestException.class, () -> mainService.getMainMissionList(sort, num, filter));
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

    private List<ParticipantDocument> prepareParticipantDocumentList(ObjectId[] ids, String email) {
        return Arrays.stream(ids)
                .map(id -> ParticipantDocument.builder()
                        .missionId(id)
                        .userEmail(email)
                        .build())
                .collect(Collectors.toList());
    }

    private List<MissionInfo> prepareMissionInfoList(ObjectId[] ids) {
        List<MissionInfo> missionInfoList = new ArrayList<>();

        for (ObjectId id : ids) {
            MissionInfo missionInfo = new MissionInfo();
            missionInfo.setId(ids[0].toString());
            missionInfoList.add(missionInfo);
        }

        return missionInfoList;
    }
}
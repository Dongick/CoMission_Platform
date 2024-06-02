package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.config.jwt.JWTUtil;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.SimpleMissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.User;
import mission.dto.user.UserMissionPost;
import mission.dto.user.UserMissionPostResponse;
import mission.dto.user.UserPostResponse;
import mission.exception.*;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import mission.repository.RefreshTokenRepository;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private MissionRepository missionRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MissionService missionService;
    @InjectMocks
    private UserService userService;

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
    @DisplayName("UserService의 logout 매서드 성공")
    void logout_Success() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String email = "test@example.com";

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지지 않도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenReturn(null);

        // when
        userService.logout(request, response);

        // then
        verify(refreshTokenRepository, times(1)).deleteByEmail(email);
        verify(response).addCookie(any(Cookie.class));

        // addCookie 메서드가 null 값을 가진 쿠키를 추가했는지 확인
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie addedCookie = cookieCaptor.getValue();
        assertNull(addedCookie.getValue());
    }

    @Test
    @DisplayName("UserService의 logout 매서드 cookie가 존재하지 않아 실패")
    void logout_cookieIsNull() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getCookies()).thenReturn(null);

        // when, then
        assertThrows(MissionAuthenticationException.class, () -> userService.logout(request, response));
    }

    @Test
    @DisplayName("UserService의 logout 매서드 RefreshToken cookie의 값이 null 이어서 실패")
    void logout_refreshTokenIsNull() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", null);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(null)).thenThrow(new MissionAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage()));

        // when, then
        assertThrows(MissionAuthenticationException.class, () -> userService.logout(request, response));
    }

    @Test
    @DisplayName("UserService의 logout 매서드 RefreshToken cookie가 만료되어서 실패")
    void logout_refreshTokenIsExpired() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenThrow(new MissionAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED, ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage()));

        // when, then
        assertThrows(MissionAuthenticationException.class, () -> userService.logout(request, response));
    }

    @Test
    @DisplayName("UserService의 logout 매서드 RefreshToken cookie의 payload값이 refresh가 아니어서 실패")
    void logout_refreshTokenIsNotRefreshCategory() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenThrow(new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage()));

        // when, then
        assertThrows(BadRequestException.class, () -> userService.logout(request, response));
    }

    @Test
    @DisplayName("UserService의 logout 매서드 RefreshToken cookie의 값이 db에 저장되어 있는 값과 달라서 실패")
    void logout_refreshTokenNotInDatabase() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenThrow(new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage()));

        // when, then
        assertThrows(BadRequestException.class, () -> userService.logout(request, response));
    }

    @Test
    @DisplayName("userPost 매서드: 참여했거나 참여하고 있는 미션이 존재하는 상황에서 성공")
    void userPost_withParticipantMission() {
        // given
        String userEmail = "test@example.com";

        ObjectId[] ids = {new ObjectId("65ea0c8007b2c737d6227bf0"), new ObjectId("65ea0c8007b2c737d6227bf2"), new ObjectId("65ea0c8007b2c737d6227bf4")};

        List<ParticipantDocument> participantDocumentList = prepareParticipantDocumentList(ids, userEmail);
        List<SimpleMissionInfo> simpleMissionInfoList = prepareMissionInfoList(ids);

        when(participantRepository.findByUserEmail(anyString())).thenReturn(participantDocumentList);
        when(missionRepository.findByIdInOrderByCreatedAtDesc(anyList())).thenReturn(simpleMissionInfoList);

        // when
        UserPostResponse userPostResponse = userService.userPost();

        // then
        Assertions.assertThat(userPostResponse.getSimpleMissionInfoList()).isEqualTo(simpleMissionInfoList);

        verify(participantRepository).findByUserEmail(anyString());
        verify(missionRepository).findByIdInOrderByCreatedAtDesc(anyList());
    }

    @Test
    @DisplayName("userPost 매서드: 참여했거나 참여하고 있는 미션이 없는 상황에서 성공")
    void userPost_withoutParticipantMission() {
        // given
        List<ParticipantDocument> participantDocumentList = new ArrayList<>();

        when(participantRepository.findByUserEmail(anyString())).thenReturn(participantDocumentList);

        // when
        UserPostResponse userPostResponse = userService.userPost();

        // then
        Assertions.assertThat(userPostResponse.getSimpleMissionInfoList()).isNull();

        verify(participantRepository).findByUserEmail(anyString());
        verify(missionRepository, never()).findByIdInOrderByCreatedAtDesc(anyList());
    }

    @Test
    @DisplayName("userMissionPost 매서드: 자신이 작성한 인증글이 5개가 넘는 미션 성공")
    void userMissionPost_overAuthentication5() {
        // given
        String userEmail = "test@example.com";
        String id = "65ea0c8007b2c737d6227bf0";
        int num = 0;

        String []textData = {"test1 data", "test2 data", "test3 data", "test4 data", "test5 data", "test6 data"};

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        MissionDocument missionDocument = MissionDocument.builder()
                .id(new ObjectId(id))
                .build();

        ParticipantDocument participantDocument = prepareTwoAuthentication(prepareParticipantDocument(id, userEmail), now, null, textData);

        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(participantRepository.findByMissionIdAndUserEmail(any(ObjectId.class), anyString())).thenReturn(Optional.of(participantDocument));

        // when
        UserMissionPostResponse userMissionPostResponse = userService.userMissionAuthenticationPost(userEmail, id, num);

        // then
        List<UserMissionPost> userMissionPostList = userMissionPostResponse.getUserMissionPostList();
        Assertions.assertThat(userMissionPostList.size()).isEqualTo(5);
        Assertions.assertThat(userMissionPostList.get(0).getTextData()).isEqualTo(textData[0]);
        Assertions.assertThat(userMissionPostList.get(1).getTextData()).isEqualTo(textData[1]);
        Assertions.assertThat(userMissionPostList.get(2).getTextData()).isEqualTo(textData[2]);
        Assertions.assertThat(userMissionPostList.get(3).getTextData()).isEqualTo(textData[3]);
        Assertions.assertThat(userMissionPostList.get(4).getTextData()).isEqualTo(textData[4]);

        verify(missionService).getMissionDocument(anyString());
        verify(participantRepository).findByMissionIdAndUserEmail(any(ObjectId.class), anyString());
    }

    @Test
    @DisplayName("userMissionPost 매서드: 자신이 작성한 인증글이 5개가 안되는 미션 성공")
    void userMissionPost_underAuthentication5() {
        // given
        String userEmail = "test@example.com";
        String id = "65ea0c8007b2c737d6227bf0";
        int num = 0;

        String []textData = {"test1 data", "test2 data", "test3 data", "test4 data"};

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        MissionDocument missionDocument = MissionDocument.builder()
                .id(new ObjectId(id))
                .build();

        ParticipantDocument participantDocument = prepareTwoAuthentication(prepareParticipantDocument(id, userEmail), now, null, textData);

        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(participantRepository.findByMissionIdAndUserEmail(any(ObjectId.class), anyString())).thenReturn(Optional.of(participantDocument));

        // when
        UserMissionPostResponse userMissionPostResponse = userService.userMissionAuthenticationPost(userEmail, id, num);

        // then
        List<UserMissionPost> userMissionPostList = userMissionPostResponse.getUserMissionPostList();
        Assertions.assertThat(userMissionPostList.size()).isEqualTo(4);
        Assertions.assertThat(userMissionPostList.get(0).getTextData()).isEqualTo(textData[0]);
        Assertions.assertThat(userMissionPostList.get(1).getTextData()).isEqualTo(textData[1]);
        Assertions.assertThat(userMissionPostList.get(2).getTextData()).isEqualTo(textData[2]);
        Assertions.assertThat(userMissionPostList.get(3).getTextData()).isEqualTo(textData[3]);

        verify(missionService).getMissionDocument(anyString());
        verify(participantRepository).findByMissionIdAndUserEmail(any(ObjectId.class), anyString());
    }

    @Test
    @DisplayName("userMissionPost 매서드: num이 0보다 클 때 성공")
    void userMissionPost_num_1() {
        // given
        String userEmail = "test@example.com";
        String id = "65ea0c8007b2c737d6227bf0";
        int num = 1;

        String []textData = {"test1 data", "test2 data", "test3 data", "test4 data", "test5 data", "test6 data"};

        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11);

        MissionDocument missionDocument = MissionDocument.builder()
                .id(new ObjectId(id))
                .build();

        ParticipantDocument participantDocument = prepareTwoAuthentication(prepareParticipantDocument(id, userEmail), now, null, textData);

        when(missionService.getMissionDocument(anyString())).thenReturn(missionDocument);
        when(participantRepository.findByMissionIdAndUserEmail(any(ObjectId.class), anyString())).thenReturn(Optional.of(participantDocument));

        // when
        UserMissionPostResponse userMissionPostResponse = userService.userMissionAuthenticationPost(userEmail, id, num);

        // then
        List<UserMissionPost> userMissionPostList = userMissionPostResponse.getUserMissionPostList();
        Assertions.assertThat(userMissionPostList.size()).isEqualTo(1);
        Assertions.assertThat(userMissionPostList.get(0).getTextData()).isEqualTo(textData[5]);

        verify(missionService).getMissionDocument(anyString());
        verify(participantRepository).findByMissionIdAndUserEmail(any(ObjectId.class), anyString());
    }

    @Test
    @DisplayName("userMissionPost 매서드: email이 로그인한 사용자와 다를 때 실패")
    void userMissionPost_email_DIFFERENT_LOGGED_USER() {
        // given
        String email = "testFailed@example.com";
        String id = "65ea0c8007b2c737d6227bf0";
        int num = 0;

        // when, then
        assertThrows(ForbiddenException.class, () -> userService.userMissionAuthenticationPost(email, id, num));
    }

    @Test
    @DisplayName("userMissionPost 매서드: num 값이 0보다 작을 때 실패")
    void userMissionPost_num_VALIDATION_FAILED() {
        // given
        String userEmail = "test@example.com";
        String id = "65ea0c8007b2c737d6227bf0";
        int num = -1;

        // when, then
        assertThrows(BadRequestException.class, () -> userService.userMissionAuthenticationPost(userEmail, id, num));
    }

    private List<ParticipantDocument> prepareParticipantDocumentList(ObjectId[] ids, String email) {
        return Arrays.stream(ids)
                .map(id -> ParticipantDocument.builder()
                        .missionId(id)
                        .userEmail(email)
                        .build())
                .collect(Collectors.toList());
    }

    private List<SimpleMissionInfo> prepareMissionInfoList(ObjectId[] ids) {
        List<SimpleMissionInfo> simpleMissionInfoList = new ArrayList<>();

        for (ObjectId id : ids) {
            SimpleMissionInfo simpleMissionInfo = new SimpleMissionInfo();
            simpleMissionInfo.setId(id.toString());
            simpleMissionInfoList.add(simpleMissionInfo);
        }

        return simpleMissionInfoList;
    }

    private ParticipantDocument prepareParticipantDocument(String missionId, String userEmail) {
        return ParticipantDocument.builder()
                .missionId(new ObjectId(missionId))
                .userEmail(userEmail)
                .authentication(new ArrayList<>())
                .build();
    }

    private ParticipantDocument prepareTwoAuthentication(ParticipantDocument participantDocument, LocalDateTime now, String photoUrl, String [] textData) {
        List<mission.document.Authentication> authenticationList = participantDocument.getAuthentication();

        for(int i = 0; i < textData.length; i++) {
            authenticationList.add(mission.document.Authentication.builder()
                    .date(now.minusDays(i + 1))
                    .photoData(photoUrl)
                    .textData(textData[i])
                    .build());
        }

        participantDocument.setAuthentication(authenticationList);

        return participantDocument;
    }
}
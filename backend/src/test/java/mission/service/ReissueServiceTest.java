package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.config.jwt.JWTUtil;
import mission.entity.RefreshTokenEntity;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReissueServiceTest {
    @Mock
    private JWTUtil jwtUtil;
    @InjectMocks
    private ReissueService reissueService;
    @Test
    @DisplayName("reissue 매서드: 성공")
    void reissue_success() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String refreshToken = "testRefreshToken";
        Cookie cookie = new Cookie("RefreshToken", refreshToken);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();

        String username = "testUser";
        String role = "ROLE_USER";
        String email = "test@example.com";

        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        Cookie newCookie = new Cookie("RefreshToken", newRefreshToken);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(refreshTokenEntity);
        when(jwtUtil.getUsername(refreshToken)).thenReturn(username);
        when(jwtUtil.getRole(refreshToken)).thenReturn(role);
        when(jwtUtil.getEmail(refreshToken)).thenReturn(email);
        when(jwtUtil.createJwt("access", username, role, email)).thenReturn(newAccessToken);
        when(jwtUtil.createJwt("refresh", username, role, email)).thenReturn(newRefreshToken);
        when(jwtUtil.createJwtCookie("RefreshToken", newRefreshToken)).thenReturn(newCookie);

        // when
        reissueService.reissue(request, response);

        // then
        verify(response).setHeader("Authorization", newAccessToken);
        verify(jwtUtil).updateRefreshToken(refreshTokenEntity, newRefreshToken);

        // addCookie 메서드가 새로운 RefreshToken 값을 가진 쿠키를 추가했는지 확인
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie addedCookie = cookieCaptor.getValue();
        Assertions.assertThat(addedCookie.getValue()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("reissue 매서드 : cookie가 존재하지 않아 실패")
    void reissue_CookieIsNull() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getCookies()).thenReturn(null);

        // when, then
        assertThrows(MissionAuthenticationException.class, () -> reissueService.reissue(request, response));
    }

    @Test
    @DisplayName("reissue 매서드 : RefreshToken cookie의 값이 null 이어서 실패")
    void reissue_refreshTokenIsNull() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", null);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(null)).thenThrow(new MissionAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage()));

        // when, then
        assertThrows(MissionAuthenticationException.class, () -> reissueService.reissue(request, response));
    }

    @Test
    @DisplayName("reissue 매서드 : RefreshToken cookie가 만료되어서 실패")
    void reissue_refreshTokenIsExpired() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenThrow(new MissionAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED, ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage()));

        // when, then
        assertThrows(MissionAuthenticationException.class, () -> reissueService.reissue(request, response));
    }

    @Test
    @DisplayName("reissue 매서드 : RefreshToken cookie의 payload값이 refresh가 아니어서 실패")
    void reissue_refreshTokenIsNotRefreshCategory() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenThrow(new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage()));

        // when, then
        assertThrows(BadRequestException.class, () -> reissueService.reissue(request, response));
    }

    @Test
    @DisplayName("reissue 매서드 : RefreshToken cookie의 값이 db에 저장되어 있는 값과 달라서 실패")
    void reissue_refreshTokenNotInDatabase() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenThrow(new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID, ErrorCode.REFRESH_TOKEN_INVALID.getMessage()));

        // when, then
        assertThrows(BadRequestException.class, () -> reissueService.reissue(request, response));
    }
}
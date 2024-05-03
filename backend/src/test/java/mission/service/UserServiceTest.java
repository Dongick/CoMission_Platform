package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.config.jwt.JWTUtil;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.User;
import mission.exception.BadRequestException;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import mission.repository.RefreshTokenRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
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
}
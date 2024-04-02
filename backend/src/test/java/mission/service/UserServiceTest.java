package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.config.jwt.JWTUtil;
import mission.dto.User;
import mission.dto.oauth2.CustomOAuth2User;
import mission.exception.MissionAuthenticationException;
import mission.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Test
    void logout_Success() {
        // Mock HttpServletRequest와 HttpServletResponse 생성
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock SecurityContextHolder 설정
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock principal 객체 생성
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(User.builder()
                .email("test@example.com")
                .role("ROLE_USER")
                .name("test")
                .build());
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(customOAuth2User);

        // Mock cookie 생성
        Cookie cookie = new Cookie("RefreshToken", "testRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // jwtUtil.validateRefreshToken 메서드가 예외를 던지지 않도록 Mock 처리
        when(jwtUtil.validateRefreshToken(anyString())).thenReturn(null);

        // userService.logout 메서드 호출
        userService.logout(request, response);

        // refreshTokenRepository.deleteByEmail 메서드가 호출되었는지 확인
        verify(refreshTokenRepository, times(1)).deleteByEmail(customOAuth2User.getEmail());

        // response에 addCookie 메서드가 호출되었는지 확인
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testLogoutWithNoCookies() {
        // Mock HttpServletRequest와 HttpServletResponse 생성
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(User.builder()
                .email("test@example.com")
                .role("ROLE_USER")
                .name("test")
                .build());

        // SecurityContextHolder의 Mock 처리
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customOAuth2User);

        // Mock cookie를 반환하지 않도록 설정
        when(request.getCookies()).thenReturn(null);

        // userService.logout 메서드를 호출하면 MissionAuthenticationException이 발생하는지 확인
        assertThrows(MissionAuthenticationException.class, () -> userService.logout(request, response));
    }
}
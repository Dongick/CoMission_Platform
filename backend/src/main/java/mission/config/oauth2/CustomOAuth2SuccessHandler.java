package mission.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.dto.oauth2.CustomOAuth2User;
import mission.entity.RefreshTokenEntity;
import mission.config.jwt.JWTUtil;
import mission.repository.RefreshTokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getName();
        String email = customOAuth2User.getEmail();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt("access", username, role, email);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, email);

        Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokenRepository.findByEmail(email);

        if(refreshTokenEntity.isPresent()) {
            jwtUtil.updateRefreshToken(refreshTokenEntity.get(), refreshToken);
        } else {
            jwtUtil.saveRefreshToken(refreshToken, email);
        }

        System.out.println(accessToken);

        String redirectUrl = "http://localhost:3000/?AccessToken=" + URLEncoder.encode(accessToken, "UTF-8") +
                "&email=" + URLEncoder.encode(email, "UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8");

        response.addCookie(jwtUtil.createJwtCookie("RefreshToken", refreshToken));

        response.sendRedirect(redirectUrl);
    }
}

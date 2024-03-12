package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.oauth2.CustomOAuth2User;
import mission.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        refreshTokenRepository.deleteByEmail(userEmail);
    }
}

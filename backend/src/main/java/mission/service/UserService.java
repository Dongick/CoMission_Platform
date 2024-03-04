package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.UserLogoutRequest;
import mission.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public Boolean logout(UserLogoutRequest userLogoutRequest) {

        CustomOAuth2User id = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(id.getEmail());

        refreshTokenRepository.deleteByEmail(userLogoutRequest.getEmail());

        return true;
    }
}

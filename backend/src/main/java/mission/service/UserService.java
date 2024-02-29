package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.CustomOAuth2User;
import mission.dto.LogoutDto;
import mission.config.jwt.JWTUtil;
import mission.repository.RefreshTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public Boolean logout(LogoutDto logoutDto) {

        CustomOAuth2User id = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(id.getEmail());

        refreshTokenRepository.deleteByEmail(id.getEmail());

        return true;
    }
}

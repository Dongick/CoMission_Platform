package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.LogoutDto;
import mission.jwt.JWTUtil;
import mission.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public Boolean logout(LogoutDto logoutDto) {
        System.out.println(logoutDto.getEmail());
        refreshTokenRepository.deleteByEmail(logoutDto.getEmail());
        return true;
    }
}

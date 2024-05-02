package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.config.jwt.JWTUtil;
import mission.dto.mission.SimpleMissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.participant.ParticipantMissionId;
import mission.dto.user.UserPostResponse;
import mission.exception.ErrorCode;
import mission.exception.MissionAuthenticationException;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import mission.repository.RefreshTokenRepository;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;

    // logout 매서드
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        // cookie에서 refresh token 찾음
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("RefreshToken")) {

                    refresh = cookie.getValue();
                }
            }
        } else {
            throw new MissionAuthenticationException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
        }

        // refreshToken 검증
        jwtUtil.validateRefreshToken(refresh);

        // db에서 refreshToken 삭제
        refreshTokenRepository.deleteByEmail(userEmail);

        // 쿠키에서 refreshToken 삭제
        Cookie cookie = new Cookie("RefreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // 사용자가 참가한 미션 목록 매서드
    @Transactional
    public UserPostResponse userPost() {
        List<SimpleMissionInfo> participantSimpleMissionInfoList = null;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        List<ParticipantMissionId> participantMissionIdList = participantRepository.findByUserEmail(userEmail);

        List<ObjectId> missionIdList = participantMissionIdList.stream()
                .map(ParticipantMissionId::getMissionId)
                .collect(Collectors.toList());

        if(!missionIdList.isEmpty()) {
            participantSimpleMissionInfoList = missionRepository.findById(missionIdList);
        }

        UserPostResponse userPostResponse = new UserPostResponse(participantSimpleMissionInfoList);

        return userPostResponse;
    }
}

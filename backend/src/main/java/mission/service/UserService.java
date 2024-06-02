package mission.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mission.config.jwt.JWTUtil;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.SimpleMissionInfo;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.user.UserMissionPost;
import mission.dto.user.UserMissionPostResponse;
import mission.dto.user.UserPostResponse;
import mission.exception.*;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import mission.repository.RefreshTokenRepository;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;
    private final MissionService missionService;

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
            log.error("RefreshToken이 존재하지 않음");
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

        List<ParticipantDocument> participantDocumentList = participantRepository.findByUserEmail(userEmail);

        List<ObjectId> missionIdList = participantDocumentList.stream()
                .map(ParticipantDocument::getMissionId)
                .collect(Collectors.toList());

        // 참가한 미션이 존재하면 참가한 모든 미션 목록
        if(!missionIdList.isEmpty()) {
            participantSimpleMissionInfoList = missionRepository.findByIdInOrderByCreatedAtDesc(missionIdList);
        }

        UserPostResponse userPostResponse = new UserPostResponse(participantSimpleMissionInfoList);

        return userPostResponse;
    }

    // 사용자가 참가한 미션 중 하나의 미션의 사용자 인증글 목록 매서드
    @Transactional
    public UserMissionPostResponse userMissionAuthenticationPost(String email, String id, int num) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        // 로그인한 사용자와 pathValue로 넘어온 email 값 비교
        if(userEmail.equals(email)) {

            // num 값이 0보다 작으면 유효성 검사 실패
            if(num < 0) {
                throw new BadRequestException(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
            }

            // 해당 미션이 존재하는지 확인
            MissionDocument missionDocument = missionService.getMissionDocument(id);

            ParticipantDocument participantDocument = getParticipantDocument(missionDocument, email);

            List<Authentication> authenticationList = participantDocument.getAuthentication();

            List<UserMissionPost> userMissionPostList = groupAndSortAuthentications(authenticationList, num);

            UserMissionPostResponse userMissionPostResponse = new UserMissionPostResponse(userMissionPostList);

            return userMissionPostResponse;
        }

        throw new ForbiddenException(ErrorCode.DIFFERENT_LOGGED_USER, ErrorCode.DIFFERENT_LOGGED_USER.getMessage());
    }

    // 인증글들을 형식에 맞춰 출력
    private List<UserMissionPost> groupAndSortAuthentications(List<Authentication> authenticationList, int num) {

        return authenticationList.stream()
                    .map(authentication -> {
                        UserMissionPost userMissionPost = UserMissionPost.builder()
                                .date(authentication.getDate())
                                .photoData(authentication.getPhotoData())
                                .textData(authentication.getTextData())
                                .build();
                        return userMissionPost;
                    })
                .sorted(Comparator.comparing(map -> map.getDate(), Comparator.reverseOrder()))
                .skip((long) num * 5)
                .limit(5)
                .collect(Collectors.toList()
                );
    }

    // 해당 미션에 해당 참가자가 존재하는지 확인
    public ParticipantDocument getParticipantDocument(MissionDocument missionDocument, String userEmail) {
        return participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PARTICIPANT_NOT_FOUND, ErrorCode.PARTICIPANT_NOT_FOUND.getMessage()));
    }
}

package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mission.dto.user.UserMissionPostResponse;
import mission.dto.user.UserPostResponse;
import mission.exception.ErrorResponse;
import mission.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @PostMapping("/logout")
    @Operation(
            summary = "사용자 로그아웃",
            description = "사용자 로그아웃 시 클라이언트에서 AccessToken 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. REFRESH_TOKEN_INVALID : refresh token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음 \t\n 3. REFRESH_TOKEN_EXPIRED : refresh token 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);

        return ResponseEntity.ok("good");
    }

    @GetMapping("/post")
    @Operation(
            summary = "사용자가 참여한 미션",
            description = "사용자가 참여한 모든 미션 제공"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserPostResponse> getUserPost() {
        UserPostResponse userPostResponse = userService.userPost();
        return ResponseEntity.ok(userPostResponse);
    }

    @GetMapping("/{email}/mission/{id}/post")
    @Operation(
            summary = "해당 미션의 자신이 작성한 인증글",
            description = "사용자가 선택한 미션에서 사용자가 작성한 미션 인증글만 제공"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자신이 작성한 인증글만 보기 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패 \t\n 3. TYPE_MISMATCH_FAILED : 잘못된 파라미터(ex 소수)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "1. DIFFERENT_LOGGED_USER : 로그인한 사용자와 다른 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<UserMissionPostResponse> getUserMissionAuthentication(
            @PathVariable String id,
            @PathVariable String email,
            @RequestParam(required = false, defaultValue = "0") int num) {

        UserMissionPostResponse userMissionPostResponse = userService.userMissionPost(email, id, num);
        return ResponseEntity.ok(userMissionPostResponse);
    }
}

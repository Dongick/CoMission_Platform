package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

}

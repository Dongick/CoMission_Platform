package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mission.dto.authentication.*;
import mission.exception.ErrorResponse;
import mission.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/{title}")
    @Operation(
            summary = "인증글 작성",
            description = "해당 미션의 오늘의 인증글 작성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "인증글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패 \t\n 2. DUPLICATE_AUTHENTICATION : 일일 인증글 중복",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> createAuthentication(final @Valid @RequestBody AuthenticationCreateRequest authenticationCreateRequest, @PathVariable String title) {
        authenticationService.createAuthentication(authenticationCreateRequest, title);

        return ResponseEntity.status(HttpStatus.CREATED).body("good");
    }

    @PutMapping("/{title}")
    @Operation(
            summary = "인증글 수정",
            description = "해당 미션의 오늘의 인증글 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님 \t\n 2. AUTHENTICATION_NOT_FOUND : 당일 인증한 인증글이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> updateAuthentication(final @Valid @RequestBody AuthenticationUpdateRequest authenticationUpdateRequest, @PathVariable String title) {
        authenticationService.updateAuthentication(authenticationUpdateRequest, title);

        return ResponseEntity.ok("good");
    }

    @DeleteMapping("/{title}")
    @Operation(
            summary = "인증글 삭제",
            description = "해당 미션의 오늘의 인증글 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "206", description = "인증글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님 \t\n 2. AUTHENTICATION_NOT_FOUND : 당일 인증한 인증글이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> deleteAuthentication(@PathVariable String title) {
        authenticationService.deleteAuthentication(title);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("good");
    }

    @GetMapping("/{title}")
    @Operation(
            summary = "인증글 보기",
            description = "해당 미션의 오늘의 인증글 보기"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 보기 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<AuthenticationListResponse> authenticationList(@PathVariable String title) {
        AuthenticationListResponse result = authenticationService.authenticationList(title);
        return ResponseEntity.ok(result);
    }
}

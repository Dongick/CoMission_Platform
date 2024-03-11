package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mission.dto.participant.ParticipantRequest;
import mission.exception.ErrorResponse;
import mission.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @PostMapping()
    @Operation(
            summary = "미션 참가",
            description = "기존 미션에 사용자 참가"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "미션 참가 성공"),
            @ApiResponse(responseCode = "400",
                    description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. UNAUTHORIZED : access token 없음 \t\n 3. MISSION_ALREADY_COMPLETED : 미션이 이미 종료됨 \t\n 4. VALIDATION_FAILED : 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "1. ALREADY_PARTICIPATED : 해당 미션에 이미 참가중",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> participantMission(final @Valid @RequestBody ParticipantRequest participantRequest) {
        participantService.participateMission(participantRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("good");
    }
}

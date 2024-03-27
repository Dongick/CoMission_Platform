package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mission.dto.mission.*;
import mission.exception.ErrorResponse;
import mission.service.MissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/mission")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;

    @PostMapping()
    @Operation(
            summary = "미션 생성",
            description = "새로운 미션을 생성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새로운 미션 생성 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "1. DUPLICATE_MISSION_NAME : 이미 존재하는 미션 명",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> createMission(
            @Valid @RequestPart(value="missionInfo") MissionCreateRequest missionCreateRequest,
            @RequestPart(value = "photoData", required = true) MultipartFile photoData) throws IOException {
        missionService.createMission(missionCreateRequest, photoData);
        return ResponseEntity.status(HttpStatus.CREATED).body("good");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "미션 수정",
            description = "미션을 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 수정 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패 \t\n 3. MISSION_ALREADY_COMPLETED : 미션이 이미 종료되어서 수정할 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "1. MISSION_MODIFICATION_NOT_ALLOWED : 해당 미션을 수정할 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "1. DUPLICATE_MISSION_NAME : 이미 존재하는 미션 명 \t\n 2. MISSION_ALREADY_STARTED : 미션이 이미 시작되서 수정할 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> updateMission(
            @Valid @RequestPart(value="missionInfo") MissionUpdateRequest missionUpdateRequest,
            @RequestPart(value = "photoData", required = true) MultipartFile photoData,
            @PathVariable String id) throws IOException{
        missionService.updateMission(missionUpdateRequest, photoData, id);

        return ResponseEntity.ok("good");
    }

    @GetMapping("/info/{id}")
    @Operation(
            summary = "미션 정보",
            description = "해당 미션 정보 보기"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 정보 보기 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<MissionInfoResponse> missionInfo(@PathVariable String id) {
        MissionInfoResponse missionInfoResponse = missionService.missionInfo(id);
        return ResponseEntity.ok(missionInfoResponse);
    }

    @PostMapping("/search")
    @Operation(
            summary = "미션 검색",
            description = "검색 화면에서 미션 제목의 일부를 입력하면 해당 제목의 일부와 일치하는 미션 목록 나열"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 검색 성공"),
            @ApiResponse(responseCode = "400", description = "1. VALIDATION_FAILED : 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<MissionSearchResponse> missionSearch(@Valid @RequestBody MissionSearchRequest missionSearchRequest) {
        MissionSearchResponse missionSearchResponse = missionService.missionSearch(missionSearchRequest);
        return ResponseEntity.ok(missionSearchResponse);
    }
}

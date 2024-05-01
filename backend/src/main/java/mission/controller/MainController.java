package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import mission.dto.main.MainResponse;
import mission.exception.ErrorResponse;
import mission.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    @GetMapping()
    @Operation(
            summary = "main 화면",
            description = "로그인이 되어있으면 해당 사용자가 참가한 모든 미션과 20개의 미션 정보 제공" +
                    "로그인이 안되어 있으면 20개의 미션 정보만 제공"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 정보 제공 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패 \t\n 3. TYPE_MISMATCH_FAILED : 잘못된 파라미터(ex 소수)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MainResponse> getMainList(@RequestParam(required = false, defaultValue = "latest") String sort,
                                                    @RequestParam(required = false, defaultValue = "0") int num,
                                                    @RequestParam(required = false, defaultValue = "all") String filter) {
        MainResponse mainResponse = mainService.getMainMissionList(sort, num, filter);
        return ResponseEntity.ok(mainResponse);
    }
}

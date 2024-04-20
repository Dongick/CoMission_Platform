package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import mission.dto.main.MainLazyLoadingResponse;
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
            summary = "초기 main 화면",
            description = "처음 main 화면에 들어갔을 때 로그인이 되어있으면 해당 사용자가 참가한 모든 미션과 20개의 미션 정보 제공" +
                    "로그인이 안되어 있으면 20개의 미션 정보만 제공"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 정보 제공 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MainResponse> getInitialList() {
        MainResponse mainResponse = mainService.getInitialMissionList();

        return ResponseEntity.ok(mainResponse);
    }

    @GetMapping("/{num}")
    @Operation(
            summary = "lazy loading 시 main 화면",
            description = "사용자가 main화면 스크롤을 내릴 시 초기 정보 이후의 정보를 제공"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 정보 제공 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MainLazyLoadingResponse> getLazyLoadingList(@PathVariable int num) {
        MainLazyLoadingResponse mainLazyLoadingResponse = mainService.getLazyLoadingMissionList(num);

        return ResponseEntity.ok(mainLazyLoadingResponse);
    }
}

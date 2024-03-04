package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import mission.dto.mission.MissionCreateRequest;
import mission.service.MissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mission")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;

    @PostMapping("create")
    @Operation(
            summary = "미션 생성",
            description = "새로운 미션을 생성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 미션 생성 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<String> createMission(@RequestBody MissionCreateRequest missionCreateRequest) {
        missionService.createMission(missionCreateRequest);
        return ResponseEntity.ok("good");
    }
}

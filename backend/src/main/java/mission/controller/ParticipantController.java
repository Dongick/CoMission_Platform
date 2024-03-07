package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import mission.dto.mission.MissionCreateRequest;
import mission.dto.participant.ParticipantMissionRequest;
import mission.service.ParticipantService;
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

    @PostMapping("create")
    @Operation(
            summary = "미션 참가",
            description = "기존 미션에 사용자 참가"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 참가 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<String> participantMission(@RequestBody ParticipantMissionRequest participantMissionRequest) {
        participantService.participateMission(participantMissionRequest);
        return ResponseEntity.ok("good");
    }
}

package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import mission.dto.participant.ParticipantRequest;
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

    @PostMapping("/create")
    @Operation(
            summary = "미션 참가",
            description = "기존 미션에 사용자 참가"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미션 참가 성공"),
            @ApiResponse(responseCode = "400", description = "이미 종료된 미션이어서 미션 참가 실패"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<String> participantMission(@RequestBody ParticipantRequest participantRequest) {
        String result = participantService.participateMission(participantRequest);
        if(result == "good") {
            return ResponseEntity.ok("good");
        } else {
            return ResponseEntity.status(400).body("bad");
        }

    }
}

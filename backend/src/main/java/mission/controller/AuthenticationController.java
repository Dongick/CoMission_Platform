package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import mission.dto.authentication.*;
import mission.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/create")
    @Operation(
            summary = "인증글 작성",
            description = "해당 미션의 오늘의 인증글 작성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "이미 오늘 인증글을 작성해서 인증글 작성 실패"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<String> createAuthentication(@RequestBody AuthenticationCreateRequest authenticationCreateRequest) {
        String result = authenticationService.createAuthentication(authenticationCreateRequest);

        if(result == "good") {
            return ResponseEntity.ok("good");
        } else {
            return ResponseEntity.status(400).body("bad");
        }

    }

    @PostMapping("/update")
    @Operation(
            summary = "인증글 수정",
            description = "해당 미션의 오늘의 인증글 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<String> updateAuthentication(@RequestBody AuthenticationUpdateRequest authenticationUpdateRequest) {
        authenticationService.updateAuthentication(authenticationUpdateRequest);

        return ResponseEntity.ok("good");
    }

    @PostMapping("/delete")
    @Operation(
            summary = "인증글 삭제",
            description = "해당 미션의 오늘의 인증글 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<String> deleteAuthentication(@RequestBody AuthenticationDeleteRequest authenticationDeleteRequest) {
        authenticationService.deleteAuthentication(authenticationDeleteRequest);

        return ResponseEntity.ok("good");
    }

    @GetMapping("/info/{title}")
    @Operation(
            summary = "인증글 보기",
            description = "해당 미션의 오늘의 인증글 보기"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 보기 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    public ResponseEntity<AuthenticationListResponse> authenticationList(@PathVariable String title) {
        AuthenticationListResponse result = authenticationService.authenticationList(title);
        return ResponseEntity.ok(result);
    }
}

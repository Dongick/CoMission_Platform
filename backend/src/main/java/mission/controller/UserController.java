package mission.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mission.dto.LogoutDto;
import mission.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> test(@RequestBody LogoutDto logoutDto) {
        userService.logout(logoutDto);

        return ResponseEntity.ok("good");
    }

}

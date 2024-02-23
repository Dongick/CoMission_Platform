package mission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainPage() {


        return "main route";
    }
}

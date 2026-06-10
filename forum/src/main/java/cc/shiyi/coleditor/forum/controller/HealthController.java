package cc.shiyi.coleditor.forum.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/ping")
    public String ping() {
        return "pong";
    }
}

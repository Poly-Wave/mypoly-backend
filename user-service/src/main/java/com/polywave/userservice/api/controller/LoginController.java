package com.polywave.userservice.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
public class LoginController {
    @GetMapping("/login")
    public Map<String, Object> login() {
        return Map.of(
            "success", true,
            "message", "Use /oauth2/authorization/kakao to start login"
        );
    }
}

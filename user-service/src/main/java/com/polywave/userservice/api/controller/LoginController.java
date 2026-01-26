package com.polywave.userservice.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.polywave.userservice.api.dto.ApiResponse;


@RestController
public class LoginController {
    @GetMapping("/login")
    public ApiResponse<String> login() {
        return ApiResponse.ok("카카오 소셜 로그인 안내", "/oauth2/authorization/kakao");
    }
}

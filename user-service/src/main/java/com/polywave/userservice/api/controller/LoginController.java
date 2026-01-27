package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public ApiResponse<String> login(HttpServletRequest request) {
        // context-path가 /users면 "/users/oauth2/authorization/kakao" 로 내려감
        // 로컬(컨텍스트패스 없으면) "/oauth2/authorization/kakao" 로 내려감
        String oauth2StartUrl = request.getContextPath() + "/oauth2/authorization/kakao";
        return ApiResponse.ok("카카오 소셜 로그인 안내", oauth2StartUrl);
    }
}

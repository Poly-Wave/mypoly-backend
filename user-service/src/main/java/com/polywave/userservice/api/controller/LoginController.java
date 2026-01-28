package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "소셜 로그인 시작/안내")
@RestController
public class LoginController {

    @Operation(
            summary = "카카오 로그인 시작 URL 조회",
            description = "카카오 OAuth2 Authorization Code 플로우 시작 URL을 반환합니다."
    )
    @GetMapping("/login")
    public ApiResponse<String> login(HttpServletRequest request) {
        // context-path가 /users면 "/users/oauth2/authorization/kakao" 로 내려감
        // 로컬(컨텍스트패스 없으면) "/oauth2/authorization/kakao" 로 내려감
        String oauth2StartUrl = request.getContextPath() + "/oauth2/authorization/kakao";
        return ApiResponse.ok("카카오 소셜 로그인 안내", oauth2StartUrl);
    }
}

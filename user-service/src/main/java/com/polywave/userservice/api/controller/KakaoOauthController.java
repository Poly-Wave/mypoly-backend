package com.polywave.userservice.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
public class KakaoOauthController {

    @PostMapping("/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> body) {
        // 실제 구현에서는 카카오 서버에 access_token을 검증 요청해야 함
        // 여기서는 accessToken 값만 받아서 echo
        String accessToken = body.get("accessToken");
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "accessToken is required"));
        }
        // 실제 카카오 사용자 정보 조회 및 회원가입/로그인 로직은 추후 구현
        return ResponseEntity.ok(Map.of(
                "result", "ok",
                "accessToken", accessToken
        ));
    }
}

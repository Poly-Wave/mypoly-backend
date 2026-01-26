package com.polywave.userservice.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/oauth2/authorization/kakao";
    }

    @GetMapping("/login/success")
    @ResponseBody
    public String loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        // 카카오에서 받은 사용자 정보 출력 (실제 서비스에서는 DB 저장/회원가입 처리)
        return "카카오 로그인 성공!<br>사용자 정보: " + oAuth2User.getAttributes();
    }
}

package com.polywave.userservice.api.controller;

import com.polywave.userservice.service.command.SocialUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final SocialUserService socialUserService;

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/oauth2/authorization/kakao";
    }

    @GetMapping("/login/success")
    @ResponseBody
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String provider = "kakao";
        Object idObj = oAuth2User.getAttribute("id");
        String providerUserId = (idObj != null) ? String.valueOf(idObj) : null;
        String nickname = null;
        String profileImageUrl = null;
        Object propertiesObj = oAuth2User.getAttribute("properties");
        if (propertiesObj instanceof Map) {
            Map<String, Object> properties = (Map<String, Object>) propertiesObj;
            profileImageUrl = properties.get("profile_image") != null ? (String) properties.get("profile_image") : null;
        }
        if (providerUserId == null) {
            Map<String, Object> errorBody = new java.util.HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "필수값 누락 (providerUserId)");
            errorBody.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }
        com.polywave.userservice.domain.UserOauth userOauth = socialUserService.saveOrGetSocialUser(
            provider, providerUserId, nickname, profileImageUrl
        );
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("provider", userOauth.getProvider());
        data.put("providerUserId", userOauth.getProviderUserId());
        String userNickname = userOauth.getUser().getNickname();
        if (userNickname != null) data.put("nickname", userNickname);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("message", "소셜 로그인 성공");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}

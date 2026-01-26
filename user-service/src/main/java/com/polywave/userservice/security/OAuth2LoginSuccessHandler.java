package com.polywave.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.api.oauth.KakaoUserInfo;
import com.polywave.userservice.api.oauth.SocialUserInfo;
import com.polywave.userservice.domain.UserOauth;
import com.polywave.userservice.service.command.SocialUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SocialUserService socialUserService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {


        if (!(authentication instanceof OAuth2AuthenticationToken token)) {
            write(response, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.fail("Unauthenticated"));
            return;
        }
        Object principal = token.getPrincipal();
        if (!(principal instanceof OAuth2User)) {
            write(response, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.fail("Unauthenticated"));
            return;
        }
        OAuth2User oAuth2User = (OAuth2User) principal;

        String provider = token.getAuthorizedClientRegistrationId(); // ex) kakao
        SocialUserInfo userInfo = switch (provider) {
            case "kakao" -> new KakaoUserInfo(oAuth2User);
            default -> null;
        };

        if (userInfo == null) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.fail("Unsupported provider: " + provider));
            return;
        }

        if (userInfo.getProviderUserId() == null) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.fail("필수값 누락 (providerUserId)"));
            return;
        }

        SocialUserService.SocialUserDto userDto = socialUserService.loginOrRegisterDto(
            userInfo.getProvider(),
            userInfo.getProviderUserId(),
            null, 
            userInfo.getProfileImageUrl()
        );

        String jwt = jwtUtil.generateToken(String.valueOf(userDto.userId()));

        SocialLoginResponse data = new SocialLoginResponse(
            userDto.userId(),
            userDto.provider(),
            userDto.providerUserId(),
            userDto.nickname(),
            userDto.profileImageUrl(),
            jwt
        );

        write(response, HttpServletResponse.SC_OK, ApiResponse.ok("소셜 로그인 성공", data));
    }

    private void write(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}

package com.polywave.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.SocialLoginResponse;
import com.polywave.userservice.application.auth.SocialUserService;
import com.polywave.userservice.application.auth.command.SocialLoginCommand;
import com.polywave.userservice.application.auth.result.SocialUserResult;
import com.polywave.userservice.security.oauth.SocialUserInfo;
import com.polywave.userservice.security.oauth.SocialUserInfoResolver;
import com.polywave.userservice.security.oauth.UnsupportedOAuth2ProviderException;
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

    private final SocialUserInfoResolver socialUserInfoResolver;

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
        if (!(principal instanceof OAuth2User oAuth2User)) {
            write(response, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.fail("Unauthenticated"));
            return;
        }

        String provider = token.getAuthorizedClientRegistrationId(); // ex) kakao

        final SocialUserInfo userInfo;
        try {
            userInfo = socialUserInfoResolver.resolve(provider, oAuth2User);
        } catch (UnsupportedOAuth2ProviderException e) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.fail(e.getMessage()));
            return;
        }

        if (userInfo.getProviderUserId() == null) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.fail("필수값 누락 (providerUserId)"));
            return;
        }

        SocialLoginCommand command = new SocialLoginCommand(
                userInfo.getProvider(),
                userInfo.getProviderUserId(),
                null,
                userInfo.getProfileImageUrl()
        );

        SocialUserResult user = socialUserService.loginOrRegister(command);

        String jwt = jwtUtil.generateToken(String.valueOf(user.userId()));

        SocialLoginResponse data = new SocialLoginResponse(
                user.userId(),
                user.provider(),
                user.providerUserId(),
                user.nickname(),
                user.profileImageUrl(),
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

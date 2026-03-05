package com.polywave.userservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polywave.security.JwtUtil;
import com.polywave.security.RefreshJwtUtil;
import com.polywave.userservice.api.dto.SocialTokenLoginRequest;
import com.polywave.userservice.api.dto.SocialTokenSignupRequest;
import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.api.dto.TermsAgreementRequest;
import com.polywave.userservice.api.dto.TokenRefreshRequest;
import com.polywave.userservice.application.auth.SocialTokenAuthService;
import com.polywave.userservice.application.auth.command.SocialTokenLoginCommand;
import com.polywave.userservice.application.auth.command.SocialTokenSignupCommand;
import com.polywave.userservice.application.auth.result.SocialLoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialTokenAuthController.class)
@Import(TestWebMvcConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class SocialTokenAuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private SocialTokenAuthService socialTokenAuthService;

        @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private RefreshJwtUtil refreshJwtUtil;

        @Test
        @DisplayName("소셜 토큰 로그인 - 정상 요청 시 200 OK와 결괏값 반환")
        void loginWithToken_Success() throws Exception {
                // given
                SocialTokenLoginRequest request = new SocialTokenLoginRequest(SocialTokenType.ACCESS_TOKEN,
                                "발급된_액세스토큰");
                SocialLoginResult mockResult = new SocialLoginResult(1L, "KAKAO", "123456", "테스트닉네임", "imgUrl",
                                "newJwt",
                                "newRefresh");

                given(socialTokenAuthService.login(any(SocialTokenLoginCommand.class))).willReturn(mockResult);

                // when & then
                mockMvc.perform(post("/auth/token/KAKAO/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(1L))
                                .andExpect(jsonPath("$.provider").value("KAKAO"))
                                .andExpect(jsonPath("$.nickname").value("테스트닉네임"))
                                .andExpect(jsonPath("$.jwt").value("newJwt"))
                                .andExpect(jsonPath("$.refreshToken").value("newRefresh"));
        }

        @Test
        @DisplayName("소셜 토큰 회원가입 - 정상 요청 시 201 CREATED 반환")
        void signupWithToken_Success() throws Exception {
                // given
                SocialTokenSignupRequest request = new SocialTokenSignupRequest(
                                SocialTokenType.ID_TOKEN,
                                "발급된_ID토큰",
                                "신규유저",
                                List.of(new TermsAgreementRequest(1L, true)));
                SocialLoginResult mockResult = new SocialLoginResult(2L, "APPLE", "appleId", "신규유저", "imgUrl",
                                "appleJwt",
                                "appleRefresh");

                given(socialTokenAuthService.signup(any(SocialTokenSignupCommand.class))).willReturn(mockResult);

                // when & then
                mockMvc.perform(post("/auth/token/APPLE/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.userId").value(2L))
                                .andExpect(jsonPath("$.provider").value("APPLE"))
                                .andExpect(jsonPath("$.jwt").value("appleJwt"));
        }

        @Test
        @DisplayName("토큰 재발급 - 정상 RefreshToken 입력 시 새 JWT 반환")
        void refresh_Success() throws Exception {
                // given
                TokenRefreshRequest request = new TokenRefreshRequest("valid_refresh_token");

                given(refreshJwtUtil.validateRefreshToken("valid_refresh_token")).willReturn(true);
                given(refreshJwtUtil.extractUserId("valid_refresh_token")).willReturn(10L);
                given(jwtUtil.createToken(10L)).willReturn("new_fresh_jwt");

                // when & then
                mockMvc.perform(post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.jwt").value("new_fresh_jwt"));
        }

        @Test
        @DisplayName("토큰 재발급 - 올바르지 않은 RefreshToken 입력 시 401 UNAUTHORIZED")
        void refresh_Fail_InvalidToken() throws Exception {
                // given
                TokenRefreshRequest request = new TokenRefreshRequest("invalid_token");
                given(refreshJwtUtil.validateRefreshToken("invalid_token")).willReturn(false);

                // when & then
                mockMvc.perform(post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }
}

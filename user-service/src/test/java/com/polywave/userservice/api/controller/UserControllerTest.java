package com.polywave.userservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polywave.userservice.api.dto.UpdateOnboardingStatusRequest;
import com.polywave.userservice.api.dto.UserUpdateProfileRequest;
import com.polywave.userservice.application.address.query.service.AddressQueryService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.user.query.result.OnboardingStatusResult;
import com.polywave.userservice.application.user.query.result.UserMeResult;
import com.polywave.userservice.application.user.query.service.UserQueryService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityStatus;
import com.polywave.security.JwtUtil;
import com.polywave.userservice.domain.Gender;
import com.polywave.userservice.domain.OnBoardingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(TestWebMvcConfig.class)
@AutoConfigureMockMvc(addFilters = false) // Security Filter 임시 비활성화 (로그인 목적으로 필요한 경우 분리)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private NicknameQueryService nicknameQueryService;

        @MockBean
        private AddressQueryService addressQueryService;

        @MockBean
        private UserQueryService userQueryService;

        @MockBean
        private UserCommandService userCommandService;

        @MockBean
        private JwtUtil jwtUtil;

        @Test
        @DisplayName("내 정보(getMe) 조회 컨트롤러 테스트")
        void getMe_Success() throws Exception {
                // given
                UserMeResult mockResult = new UserMeResult(
                                1L, "KAKAO", "123", "testUser", OnBoardingStatus.COMPLETE,
                                Gender.MAN, "19900101", "http://image.url",
                                "서울", "강남구", "역삼동", "서울 강남구 역삼동");
                given(userQueryService.getMe(anyLong())).willReturn(mockResult);

                // when & then
                mockMvc.perform(get("/me"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(1))
                                .andExpect(jsonPath("$.nickname").value("testUser"))
                                .andExpect(jsonPath("$.onboardingStatus").value("COMPLETE"));
        }

        @Test
        @DisplayName("닉네임 중복 검사 확인")
        void checkNicknameAvailability() throws Exception {
                // given
                NicknameAvailabilityResult result = new NicknameAvailabilityResult(true,
                                NicknameAvailabilityStatus.AVAILABLE);
                given(nicknameQueryService.isNicknameAvailable("당근도사123")).willReturn(result);

                // when & then
                mockMvc.perform(get("/nicknames/availability")
                                .param("nickname", "당근도사123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.available").value(true))
                                .andExpect(jsonPath("$.status").value("AVAILABLE"));
        }

        @Test
        @DisplayName("랜덤 닉네임 생성 확인")
        void getRandomNickname() throws Exception {
                // given
                RandomNicknameResult result = new RandomNicknameResult("randomUser123");
                given(nicknameQueryService.generateRandomNickname()).willReturn(result);

                // when & then
                mockMvc.perform(get("/nicknames/random"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nickname").value("randomUser123"));
        }

        @Test
        @DisplayName("프로필 업데이트 성공 확인")
        void updateProfile_Success() throws Exception {
                // given
                UserUpdateProfileRequest request = new UserUpdateProfileRequest(Gender.MAN, "19900101", "서울", "강남구",
                                "역삼동");

                // when & then
                mockMvc.perform(patch("/me/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("온보딩 상태 업데이트 성공 확인")
        void updateOnboardingStatus_Success() throws Exception {
                // given
                UpdateOnboardingStatusRequest request = new UpdateOnboardingStatusRequest(OnBoardingStatus.CATEGORY);

                // when & then
                mockMvc.perform(patch("/{userId}/onboarding-status", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("온보딩 상태 업데이트 실패 - 다른 사람의 ID를 수정하려 할 경우 예외 발생")
        void updateOnboardingStatus_Forbidden() throws Exception {
                // given
                UpdateOnboardingStatusRequest request = new UpdateOnboardingStatusRequest(OnBoardingStatus.CATEGORY);

                // when & then
                // 여기서 Test ArgumentResolver가 무조건 1L을 반환하게 되며, 경로의 {userId} (2L)가 다름
                mockMvc.perform(patch("/{userId}/onboarding-status", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                // GlobalExceptionHandler가 연동되지 않았으므로 NestedServletException으로 예외 발생, 여기서는 해당 예외
                                // 자체 또는 HTTP 상태를 예상
                                // (일반적인 커스텀 예외 핸들러가 없는 단일 WebMvcTest 이므로 UserValidationException 예외가 밖으로 던져지는지
                                // 확인)
                                .andExpect(result -> org.junit.jupiter.api.Assertions.assertTrue(result
                                                .getResolvedException() instanceof com.polywave.userservice.common.exception.UserValidationException));
        }

        @Test
        @DisplayName("온보딩 상태 조회 성공 확인")
        void getOnboardingStatus_Success() throws Exception {
                // given
                OnboardingStatusResult result = new OnboardingStatusResult(OnBoardingStatus.ONBOARDING);
                given(userQueryService.getOnboardingStatus(anyLong())).willReturn(result);

                // when & then
                mockMvc.perform(get("/{userId}/onboarding-status", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.onboardingStatus").value("ONBOARDING")); // OnboardingStatusResponse의
                                                                                                // 필드에 매핑되는 경우
                                                                                                // (현재 구현에 따라 응답 형식 확인)
        }

        @Test
        @DisplayName("주소 검색 조회 성공 확인")
        void searchAddress_Success() throws Exception {
                // given
                com.polywave.userservice.application.address.query.result.AddressSearchResult result = new com.polywave.userservice.application.address.query.result.AddressSearchResult(
                                1, 1, 10, java.util.Collections.singletonList(
                                                new com.polywave.userservice.application.address.query.result.AddressInfoResult(
                                                                "서울", "강남구",
                                                                "역삼동")));
                given(addressQueryService.searchAddress(anyString(), anyInt(), anyInt())).willReturn(result);

                // when & then
                mockMvc.perform(get("/addresses")
                                .param("keyword", "역삼동")
                                .param("currentPage", "1")
                                .param("countPerPage", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalCount").value(1))
                                .andExpect(jsonPath("$.addresses[0].sido").value("서울"));
        }
}

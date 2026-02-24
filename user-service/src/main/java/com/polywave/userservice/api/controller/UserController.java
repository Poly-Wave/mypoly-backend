package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.common.dto.ApiResponse;
import com.polywave.userservice.api.dto.*;
import com.polywave.userservice.api.spec.UserApi;
import com.polywave.userservice.application.address.query.result.AddressSearchResult;
import com.polywave.userservice.application.address.query.service.AddressQueryService;
import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

        private final NicknameQueryService nicknameQueryService;
        private final AddressQueryService addressQueryService;
        private final UserCommandService userCommandService;

        @Override
        public ResponseEntity<ApiResponse<NicknameAvailabilityResponse>> checkNicknameAvailability(
                        NicknameAvailabilityRequest request) {
                NicknameAvailabilityResult result = nicknameQueryService.isNicknameAvailable(request.nickname());
                return ResponseEntity.ok(
                                ApiResponse.ok(
                                                "닉네임 사용 가능 여부 조회 성공",
                                                NicknameAvailabilityResponse.of(result.available())));
        }

        @Override
        public ResponseEntity<ApiResponse<RandomNicknameResponse>> getRandomNickname() {
                RandomNicknameResult result = nicknameQueryService.generateRandomNickname();
                return ResponseEntity.ok(
                                ApiResponse.ok(
                                                "랜덤 닉네임 생성 성공",
                                                RandomNicknameResponse.of(result.nickname())));
        }

        @Override
        public ResponseEntity<ApiResponse<Void>> updateProfile(
                        UserUpdateProfileRequest request,
                        @LoginUser Long userId) {

                UserUpdateProfileCommand updateProfileCommmand = new UserUpdateProfileCommand(
                                request.gender(),
                                request.birthDate(),
                                request.sido(),
                                request.sigungu(),
                                request.emdName());
                userCommandService.updateUserProfile(userId, updateProfileCommmand);
                return ResponseEntity.ok(ApiResponse.ok("프로필 수정 성공"));
        }

        @Override
        public ResponseEntity<ApiResponse<Void>> updateOnboardingStatus(
                        Long userId,
                        UpdateOnboardingStatusRequest request,
                        @LoginUser Long authenticatedUserId) {
                if (!authenticatedUserId.equals(userId)) {
                        throw new org.springframework.security.access.AccessDeniedException("본인만 수정할 수 있습니다.");
                }
                userCommandService.updateUserOnboardingStatus(userId, request.onboardingStatus());
                return ResponseEntity.ok(ApiResponse.ok("온보딩 상태 업데이트 성공"));
        }

        @Override
        public ResponseEntity<ApiResponse<AddressSearchResponse>> searchAddress(AddressSearchRequest request) {
                AddressSearchResult result = addressQueryService.searchAddress(
                                request.keyword(),
                                request.currentPage(),
                                request.countPerPage());

                return ResponseEntity.ok(
                                ApiResponse.ok(
                                                "주소 검색 성공",
                                                convertToResponse(result)));
        }

        private AddressSearchResponse convertToResponse(AddressSearchResult result) {
                return new AddressSearchResponse(
                                result.totalCount(),
                                result.currentPage(),
                                result.countPerPage(),
                                result.addresses().stream()
                                                .map(a -> new AddressInfoResponse(a.sido(), a.sigungu(), a.emdName()))
                                                .toList());
        }
}

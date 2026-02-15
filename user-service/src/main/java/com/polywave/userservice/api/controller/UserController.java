package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.*;
import com.polywave.userservice.api.spec.UserApi;
import com.polywave.userservice.application.address.query.result.AddressSearchResult;
import com.polywave.userservice.application.address.query.service.AddressQueryService;
import com.polywave.userservice.application.nickname.command.service.NicknameCommandService;
import com.polywave.userservice.application.user.command.UserUpdateProfileCommmand;
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

        private final NicknameCommandService nicknameCommandService;
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
        public ResponseEntity<ApiResponse<Void>> assignNickname(
                        NicknameCreateRequest request,
                        @LoginUser Long userId) {
                nicknameCommandService.assignNickname(userId, request.nickname());
                return ResponseEntity.ok(ApiResponse.ok("닉네임 설정 성공"));
        }

        @Override
        public ResponseEntity<ApiResponse<Void>> updateProfile(
                        UserUpdateProfileRequest request,
                        @LoginUser Long userId) {

                UserUpdateProfileCommmand updateProfileCommmand = new UserUpdateProfileCommmand(
                        request.gender(),
                        request.birthDate(),
                        request.sido(),
                        request.sigungu(),
                        request.emdName()
                );
                userCommandService.updateUserProfile(userId, updateProfileCommmand);
                return ResponseEntity.ok(ApiResponse.ok("프로필 수정 성공"));
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

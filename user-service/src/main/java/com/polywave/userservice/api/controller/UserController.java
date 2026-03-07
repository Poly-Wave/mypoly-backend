package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.*;
import com.polywave.userservice.api.spec.UserApi;
import com.polywave.userservice.application.address.query.result.AddressSearchResult;
import com.polywave.userservice.application.address.query.service.AddressQueryService;
import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import com.polywave.userservice.application.user.query.result.OnboardingStatusResult;
import com.polywave.userservice.application.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import com.polywave.userservice.common.exception.UserErrorCode;
import com.polywave.userservice.common.exception.UserValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

        private final NicknameQueryService nicknameQueryService;
        private final AddressQueryService addressQueryService;
        private final UserQueryService userQueryService;
        private final UserCommandService userCommandService;

        @Override
        public ResponseEntity<UserMeResponse> getMe(@LoginUser Long userId) {
                var result = userQueryService.getMe(userId);

                return ResponseEntity.ok(
                                UserMeResponse.of(
                                                result.userId(),
                                                result.provider(),
                                                result.providerUserId(),
                                                result.nickname(),
                                                result.onboardingStatus(),
                                                result.gender(),
                                                result.birthDate(),
                                                result.profileImageUrl(),
                                                result.sido(),
                                                result.sigungu(),
                                                result.emdName(),
                                                result.address()
                                )
                );
        }

        @Override
        public ResponseEntity<NicknameAvailabilityResponse> checkNicknameAvailability(
                        NicknameAvailabilityRequest request) {
                NicknameAvailabilityResult result = nicknameQueryService.isNicknameAvailable(request.nickname());
                return ResponseEntity.ok(NicknameAvailabilityResponse.of(result.available(), result.status()));
        }

        @Override
        public ResponseEntity<RandomNicknameResponse> getRandomNickname() {
                RandomNicknameResult result = nicknameQueryService.generateRandomNickname();
                return ResponseEntity.ok(RandomNicknameResponse.of(result.nickname()));
        }

        @Override
        public ResponseEntity<Void> updateProfile(
                        UserUpdateProfileRequest request,
                        @LoginUser Long userId) {

                UserUpdateProfileCommand updateProfileCommmand = new UserUpdateProfileCommand(
                                request.gender(),
                                request.birthDate(),
                                request.sido(),
                                request.sigungu(),
                                request.emdName());
                userCommandService.updateUserProfile(userId, updateProfileCommmand);
                return ResponseEntity.ok().build();
        }

        @Override
        public ResponseEntity<Void> updateOnboardingStatus(
                        Long userId,
                        UpdateOnboardingStatusRequest request,
                        @LoginUser Long authenticatedUserId) {
                if (!authenticatedUserId.equals(userId)) {
                        throw new UserValidationException(UserErrorCode.FORBIDDEN_NOT_OWNER);
                }
                userCommandService.updateUserOnboardingStatus(userId, request.onboardingStatus());
                return ResponseEntity.ok().build();
        }

        @Override
        public ResponseEntity<AddressSearchResponse> searchAddress(AddressSearchRequest request) {
                AddressSearchResult result = addressQueryService.searchAddress(
                                request.keyword(),
                                request.currentPage(),
                                request.countPerPage());

                return ResponseEntity.ok(convertToResponse(result));
        }

        @Override
        public ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(Long userId) {
                OnboardingStatusResult result = userQueryService
                                .getOnboardingStatus(userId);
                return ResponseEntity.ok(OnboardingStatusResponse.of(result.status()));
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
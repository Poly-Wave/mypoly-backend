package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.NicknameAvailabilityRequest;
import com.polywave.userservice.api.dto.NicknameAvailabilityResponse;
import com.polywave.userservice.api.dto.NicknameCreateRequest;
import com.polywave.userservice.api.dto.RandomNicknameResponse;
import com.polywave.userservice.api.spec.UserApi;
import com.polywave.userservice.application.nickname.command.service.NicknameCommandService;
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
}

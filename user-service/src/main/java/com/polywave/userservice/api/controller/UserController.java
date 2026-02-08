package com.polywave.userservice.api.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.polywave.userservice.annotation.LoginUser;
import com.polywave.userservice.api.dto.*;
import com.polywave.userservice.application.nickname.service.NicknameCommandService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final NicknameCommandService nicknameCommandService;
    private final NicknameQueryService nicknameQueryService;

    @GetMapping("/nicknames/availability")
    public ResponseEntity<ApiResponse<NicknameAvailabilityResponse>> checkNicknameAvailability(
            @Valid NicknameAvailabilityRequest request
    ) {
        NicknameAvailabilityResult availabilityResult = nicknameQueryService.isNicknameAvailable(request.nickname());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(NicknameAvailabilityResponse.of(availabilityResult.available())));
    }

    @GetMapping("/nicknames/random")
    public ResponseEntity<ApiResponse<RandomNicknameResponse>> getRandomNickname() {
        RandomNicknameResult randomNicknameResult = nicknameQueryService.generateRandomNickname();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(RandomNicknameResponse.of(randomNicknameResult.nickname()))
                );
    }

    @PostMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> assignNickname(
            @Valid @RequestBody NicknameCreateRequest request, @LoginUser Long userId
    ) {

        // 1. QueryService로 조회 (CQRS 유지하기 위함)
        NicknameAvailabilityResult availabilityResult = nicknameQueryService.isNicknameAvailable(request.nickname());

        // 2. 사용자 닉네임 할당(변경)
        nicknameCommandService.assignNickname(userId, request.nickname(), availabilityResult);

        return ResponseEntity.status(CREATED).body(ApiResponse.ok("닉네임 생성 성공"));
    }

}

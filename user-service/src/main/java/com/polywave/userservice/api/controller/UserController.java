package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.NicknameAvailabilityRequest;
import com.polywave.userservice.api.dto.NicknameAvailabilityResponse;
import com.polywave.userservice.api.dto.NicknameCreateRequest;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.user.query.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 온보딩/닉네임 관련 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Operation(summary = "닉네임 사용 가능 여부 조회", description = "쿼리 파라미터 nickname으로 사용 가능 여부를 반환합니다.")
    @GetMapping("/nicknames/availability")
    public ResponseEntity<ApiResponse<NicknameAvailabilityResponse>> checkNicknameAvailability(
            @ParameterObject @Valid NicknameAvailabilityRequest request
    ) {
        boolean available = userQueryService.isNicknameAvailable(request.nickname());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(NicknameAvailabilityResponse.of(available)));
    }

    @Operation(summary = "닉네임 생성", description = "JWT 인증이 필요합니다. Authorize에 토큰 입력 후 호출하세요.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/nicknames")
    public ResponseEntity<ApiResponse<Void>> createNickname(
            @Valid @RequestBody NicknameCreateRequest request,
            @LoginUser Long userId
    ) {
        userCommandService.createNickname(userId, request.nickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("닉네임이 생성 성공"));
    }
}

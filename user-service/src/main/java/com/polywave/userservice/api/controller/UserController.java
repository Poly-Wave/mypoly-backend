package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.NicknameAvailabilityRequest;
import com.polywave.userservice.api.dto.NicknameAvailabilityResponse;
import com.polywave.userservice.api.dto.NicknameCreateRequest;
import com.polywave.userservice.api.dto.RandomNicknameResponse;
import com.polywave.userservice.application.nickname.command.service.NicknameCommandService;
import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;
import com.polywave.userservice.application.nickname.query.service.NicknameQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 온보딩/닉네임 관련 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final NicknameCommandService nicknameCommandService;
    private final NicknameQueryService nicknameQueryService;

    @Operation(summary = "닉네임 사용 가능 여부 조회", description = "쿼리 파라미터 nickname으로 사용 가능 여부를 반환합니다.")
    @GetMapping("/nicknames/availability")
    public ResponseEntity<ApiResponse<NicknameAvailabilityResponse>> checkNicknameAvailability(
            @ParameterObject @Valid NicknameAvailabilityRequest request
    ) {
        NicknameAvailabilityResult result = nicknameQueryService.isNicknameAvailable(request.nickname());
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "닉네임 사용 가능 여부 조회 성공",
                        NicknameAvailabilityResponse.of(result.available())
                )
        );
    }

    @Operation(summary = "랜덤 닉네임 생성", description = "서버에서 생성한 랜덤 닉네임을 반환합니다.")
    @GetMapping("/nicknames/random")
    public ResponseEntity<ApiResponse<RandomNicknameResponse>> getRandomNickname() {
        RandomNicknameResult result = nicknameQueryService.generateRandomNickname();
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "랜덤 닉네임 생성 성공",
                        RandomNicknameResponse.of(result.nickname())
                )
        );
    }

    @Operation(summary = "내 닉네임 설정", description = "JWT 인증이 필요합니다. Authorize에 토큰 입력 후 호출하세요.")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> assignNickname(
            @Valid @RequestBody NicknameCreateRequest request,
            @LoginUser Long userId
    ) {
        nicknameCommandService.assignNickname(userId, request.nickname());
        return ResponseEntity.ok(ApiResponse.ok("닉네임 설정 성공"));
    }
}

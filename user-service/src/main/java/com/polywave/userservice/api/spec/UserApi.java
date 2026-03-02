package com.polywave.userservice.api.spec;

import com.polywave.userservice.api.dto.AddressSearchRequest;
import com.polywave.userservice.api.dto.AddressSearchResponse;
import com.polywave.userservice.api.dto.NicknameAvailabilityRequest;
import com.polywave.userservice.api.dto.NicknameAvailabilityResponse;
import com.polywave.userservice.api.dto.OnboardingStatusResponse;
import com.polywave.userservice.api.dto.RandomNicknameResponse;
import com.polywave.userservice.api.dto.UpdateOnboardingStatusRequest;
import com.polywave.userservice.api.dto.UserMeResponse;
import com.polywave.userservice.api.dto.UserUpdateProfileRequest;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.userservice.api.example.UserApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "사용자 정보(닉네임/프로필/주소) 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface UserApi {

        @Operation(summary = "내 정보 조회", description = """
                        로그인한 사용자의 정보를 조회합니다.

                        포함 정보
                        - 온보딩 상태
                        - 닉네임
                        - 프로필(성별/생년월일/프로필 이미지/주소)

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = UserApiExamples.EXAMPLE_USER_NOT_FOUND))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/me")
        ResponseEntity<UserMeResponse> getMe(@Parameter(hidden = true) Long userId);

        @Operation(summary = "닉네임 사용 가능 여부 조회", description = """
                        쿼리 파라미터 `nickname`으로 닉네임 사용 가능 여부를 반환합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 불량", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "요청 값 검증 실패", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "권한 없음", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/nicknames/availability")
        ResponseEntity<NicknameAvailabilityResponse> checkNicknameAvailability(
                        @ParameterObject @Valid NicknameAvailabilityRequest request);

        @Operation(summary = "랜덤 닉네임 생성", description = """
                        서버에서 랜덤 닉네임을 생성해 반환합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "권한 없음", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 (닉네임 생성 실패 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "닉네임 생성 실패", value = UserApiExamples.EXAMPLE_NICKNAME_GENERATION_FAILED),
                                        @ExampleObject(name = "닉네임 소스 데이터 없음", value = UserApiExamples.EXAMPLE_NICKNAME_DATA_NOT_FOUND),
                                        @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)
                        }))
        })
        @GetMapping("/nicknames/random")
        ResponseEntity<RandomNicknameResponse> getRandomNickname();

        @Operation(summary = "주소 검색", description = """
                        행정구역(시도/시군구/읍면동) 주소 검색 결과를 반환합니다.
                        DB에서 직접 조회하며, 검색어에 해당하는 읍면동 정보를 포함합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "요청 값 검증 실패", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/addresses")
        ResponseEntity<AddressSearchResponse> searchAddress(
                        @ParameterObject @Valid AddressSearchRequest request);

        @Operation(summary = "사용자 프로필 수정", description = """
                        사용자의 성별, 생년월일, 거주지역(시도/시군구/읍면동) 정보를 수정합니다.
                        - 온보딩 상태가 CATEGORY일 때만 수정 가능합니다.
                        - 수정 성공 시 온보딩 상태는 COMPLETE로 자동 변경됩니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 불량", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "요청 값 검증 실패", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR),
                                        @ExampleObject(name = "온보딩 상태 (CATEGORY 아님) 오류", value = UserApiExamples.EXAMPLE_INVALID_ONBOARDING_STATUS_FOR_PROFILE_UPDATE)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PatchMapping("/me/profile")
        ResponseEntity<Void> updateProfile(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 프로필 정보", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserUpdateProfileRequest.class), examples = @ExampleObject(name = "요청 예시", value = UserApiExamples.EXAMPLE_UPDATE_PROFILE_REQUEST)))
                        @RequestBody @Valid UserUpdateProfileRequest request,
                        @Parameter(hidden = true) Long userId);

        @Operation(summary = "온보딩 상태 업데이트", description = """
                        사용자 온보딩 상태를 업데이트할 때 호출.
                        JWT로 본인 확인 후 path의 userId와 일치할 때만 수정 가능.

                        인증
                        - JWT 인증이 필요합니다.
                        - bill-service 호출 시 클라이언트의 Authorization 헤더를 그대로 전달하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업데이트 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "요청 값 검증 실패", value = UserApiExamples.EXAMPLE_UPDATE_ONBOARDING_STATUS_VALIDATION_ERROR))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인만 수정 가능", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "권한 없음", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = UserApiExamples.EXAMPLE_USER_NOT_FOUND))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PatchMapping("/{userId}/onboarding-status")
        ResponseEntity<Void> updateOnboardingStatus(
                        @Parameter(description = "사용자 ID") @PathVariable Long userId,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "업데이트할 온보딩 상태", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateOnboardingStatusRequest.class), examples = @ExampleObject(name = "요청 예시", value = UserApiExamples.EXAMPLE_UPDATE_ONBOARDING_STATUS_REQUEST)))
                        @RequestBody @Valid UpdateOnboardingStatusRequest request,
                        @Parameter(hidden = true) Long authenticatedUserId);

        @Operation(summary = "온보딩 상태 조회", description = """
                        사용자의 온보딩 상태를 조회합니다.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = UserApiExamples.EXAMPLE_USER_NOT_FOUND))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/{userId}/onboarding-status")
        ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(
                        @Parameter(description = "사용자 ID") @PathVariable Long userId);
}
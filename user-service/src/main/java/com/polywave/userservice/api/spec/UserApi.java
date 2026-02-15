package com.polywave.userservice.api.spec;

import com.polywave.userservice.api.dto.AddressSearchRequest;
import com.polywave.userservice.api.dto.AddressSearchResponse;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.NicknameAvailabilityRequest;
import com.polywave.userservice.api.dto.NicknameAvailabilityResponse;
import com.polywave.userservice.api.dto.NicknameCreateRequest;
import com.polywave.userservice.api.dto.RandomNicknameResponse;
import com.polywave.userservice.api.dto.UserUpdateProfileRequest;
import com.polywave.userservice.api.example.CommonApiExamples;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "사용자 정보(닉네임/프로필/주소) 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface UserApi {

        @Operation(summary = "닉네임 사용 가능 여부 조회", description = """
                        쿼리 파라미터 `nickname`으로 닉네임 사용 가능 여부를 반환합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = UserApiExamples.EXAMPLE_NICKNAME_AVAILABILITY_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "검증 실패 예시", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "403 예시", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/nicknames/availability")
        ResponseEntity<ApiResponse<NicknameAvailabilityResponse>> checkNicknameAvailability(
                        @ParameterObject @Valid NicknameAvailabilityRequest request);

        @Operation(summary = "랜덤 닉네임 생성", description = """
                        서버에서 랜덤 닉네임을 생성해 반환합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = UserApiExamples.EXAMPLE_RANDOM_NICKNAME_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "403 예시", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/nicknames/random")
        ResponseEntity<ApiResponse<RandomNicknameResponse>> getRandomNickname();

        @Operation(summary = "내 닉네임 설정", description = """
                        로그인 사용자의 닉네임을 설정(또는 변경)합니다.

                        예외 정책
                        - 요청 값 검증 실패: 400
                        - 금칙어 포함: 400
                        - 중복 닉네임: 409
                        - 사용자 없음: 404

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "설정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = UserApiExamples.EXAMPLE_ASSIGN_NICKNAME_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 금칙어 포함", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "검증 실패 예시", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR),
                                        @ExampleObject(name = "금칙어 포함 예시", value = UserApiExamples.EXAMPLE_FORBIDDEN_NICKNAME)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "403 예시", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "404 예시", value = UserApiExamples.EXAMPLE_USER_NOT_FOUND))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "중복 닉네임 예시", value = UserApiExamples.EXAMPLE_DUPLICATE_NICKNAME))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PatchMapping("/me/nickname")
        ResponseEntity<ApiResponse<Void>> assignNickname(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "설정할 닉네임", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = NicknameCreateRequest.class), examples = @ExampleObject(name = "요청 예시", value = UserApiExamples.EXAMPLE_ASSIGN_NICKNAME_REQUEST))) @Valid @RequestBody NicknameCreateRequest request,
                        @Parameter(hidden = true) Long userId);

        @Operation(summary = "주소 검색", description = """
                        행정구역(시도/시군구/읍면동) 주소 검색 결과를 반환합니다.
                        DB에서 직접 조회하며, 검색어에 해당하는 읍면동 정보를 포함합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = UserApiExamples.EXAMPLE_ADDRESS_SEARCH_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "검증 실패 예시", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping("/addresses")
        ResponseEntity<ApiResponse<AddressSearchResponse>> searchAddress(
                        @ParameterObject @Valid AddressSearchRequest request);

        @Operation(summary = "사용자 프로필 수정", description = """
                        사용자의 성별, 생년월일, 거주지역(시도/시군구/읍면동) 정보를 수정합니다.

                        인증
                        - JWT 인증이 필요합니다.
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = UserApiExamples.EXAMPLE_UPDATE_PROFILE_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "검증 실패 예시", value = UserApiExamples.EXAMPLE_VALIDATION_ERROR))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PatchMapping("/me/profile")
        ResponseEntity<ApiResponse<Void>> updateProfile(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 프로필 정보", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserUpdateProfileRequest.class), examples = @ExampleObject(name = "요청 예시", value = UserApiExamples.EXAMPLE_UPDATE_PROFILE_REQUEST))) @RequestBody @Valid UserUpdateProfileRequest request,
                        @Parameter(hidden = true) Long userId);
}

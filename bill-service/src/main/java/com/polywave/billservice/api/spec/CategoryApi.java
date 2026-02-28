package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.CategoryInterestUpdateRequest;
import com.polywave.billservice.api.dto.CategoryResponse;
import com.polywave.billservice.api.example.CategoryApiExamples;
import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Category", description = "의안 카테고리 및 사용자 관심 카테고리 API")
@RequestMapping("/categories")
public interface CategoryApi {

        @Operation(summary = "카테고리 목록 조회", description = """
                        현재 **활성화(is_active=true)** 된 카테고리 목록을 반환합니다.

                        - 반환 순서: `displayOrder` 오름차순
                        - 인증: 불필요(공개 API)
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping
        ResponseEntity<List<CategoryResponse>> getCategories();

        @Operation(summary = "내 관심 카테고리 저장(갱신)", description = """
                        로그인 사용자의 관심 카테고리 목록을 저장합니다. (JWT 인증 필요)

                        동작 규칙
                        - 요청으로 받은 `categoryIds`를 기준으로 **사용자의 관심 카테고리를 갱신**합니다.
                          - 기존 관심사 중 요청에 없는 항목은 삭제
                          - 요청에 새로 추가된 항목은 추가
                        - **중복 ID는 자동으로 제거**됩니다.
                        - **존재하지 않거나 비활성화된 카테고리 ID는 무시**됩니다. (에러로 실패시키지 않음)

                        인증
                        - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                        """)
        @SecurityRequirement(name = "bearerAuth")
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 에러", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "요청 값 검증 실패", value = CategoryApiExamples.EXAMPLE_BAD_REQUEST_VALIDATION),
                                        @ExampleObject(name = "온보딩 상태 오류", value = CategoryApiExamples.EXAMPLE_INVALID_ONBOARDING_STATUS)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED),
                                        @ExampleObject(name = "JWT 토큰 누락", value = CategoryApiExamples.EXAMPLE_MISSING_JWT_TOKEN)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "권한 없음", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "사용자 서비스 API 호출 실패", value = CategoryApiExamples.EXAMPLE_USER_SERVICE_API_FAILED),
                                        @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)
                        }))
        })
        @PostMapping("/interests")
        ResponseEntity<Void> updateInterests(
                        @Parameter(hidden = true) Long userId,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "저장할 관심 카테고리 ID 목록", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryInterestUpdateRequest.class), examples = @ExampleObject(name = "요청 예시", value = CategoryApiExamples.EXAMPLE_UPDATE_INTERESTS_REQUEST))) @RequestBody @Valid CategoryInterestUpdateRequest request);

        @Operation(summary = "내 관심 카테고리 저장 (온보딩 전용)", description = """
                        사용자 **온보딩 과정에서** 관심 카테고리 목록을 저장합니다. (JWT 인증 필요)
                        - 온보딩 상태가 SIGNUP 또는 ONBOARDING일 때만 변경 가능합니다.
                        - 성공 시 사용자 온보딩 상태를 CATEGORY로 업데이트합니다.
                        - 저장 로직은 일반 관심사 저장과 동일합니다.
                        """)
        @SecurityRequirement(name = "bearerAuth")
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 및 상태 업데이트 성공"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패 (또는 잘못된 온보딩 상태)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "요청 값 검증 실패", value = CategoryApiExamples.EXAMPLE_BAD_REQUEST_VALIDATION),
                                        @ExampleObject(name = "온보딩 상태 오류", value = CategoryApiExamples.EXAMPLE_INVALID_ONBOARDING_STATUS)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED),
                                        @ExampleObject(name = "JWT 토큰 누락", value = CategoryApiExamples.EXAMPLE_MISSING_JWT_TOKEN)
                        })),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "권한 없음", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                                        @ExampleObject(name = "사용자 서비스 API 호출 실패", value = CategoryApiExamples.EXAMPLE_USER_SERVICE_API_FAILED),
                                        @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)
                        }))
        })
        @PostMapping("/onboarding/interests")
        ResponseEntity<Void> updateOnboardingInterests(
                        @Parameter(hidden = true) Long userId,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "저장할 관심 카테고리 ID 목록", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryInterestUpdateRequest.class), examples = @ExampleObject(name = "요청 예시", value = CategoryApiExamples.EXAMPLE_UPDATE_INTERESTS_REQUEST))) @RequestBody @Valid CategoryInterestUpdateRequest request);
}

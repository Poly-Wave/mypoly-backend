package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.CategoryInterestUpdateRequest;
import com.polywave.billservice.api.dto.CategoryResponse;
import com.polywave.billservice.api.example.CategoryApiExamples;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.billservice.application.category.UserBillInterestAppService;
import com.polywave.billservice.application.category.command.service.CategoryInterestCommand;
import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.application.category.query.service.CategoryQueryService;
import com.polywave.security.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category", description = "의안 카테고리 및 사용자 관심 카테고리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

        private final CategoryQueryService categoryQueryService;
        private final UserBillInterestAppService userBillInterestAppService;

        @Operation(summary = "카테고리 목록 조회", description = """
                        현재 **활성화(is_active=true)** 된 카테고리 목록을 반환합니다.

                        - 반환 순서: `displayOrder` 오름차순
                        - 인증: 불필요(공개 API)
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = CategoryApiExamples.EXAMPLE_GET_CATEGORIES_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @GetMapping
        public ResponseEntity<List<CategoryResponse>> getCategories() {
                List<CategoryResult> results = categoryQueryService.getActiveCategories();

                List<CategoryResponse> categories = results.stream()
                                .map(CategoryResponse::from)
                                .toList();

                return ResponseEntity.ok(categories);
        }

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
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "성공 응답 예시", value = CategoryApiExamples.EXAMPLE_UPDATE_INTERESTS_OK))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "검증 실패 예시", value = CategoryApiExamples.EXAMPLE_BAD_REQUEST_VALIDATION))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요(JWT 누락/만료/위조)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "401 예시", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "403 예시", value = CommonApiExamples.EXAMPLE_FORBIDDEN))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "서버 오류 예시", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)))
        })
        @PostMapping("/interests")
        public ResponseEntity<Void> updateInterests(
                        @Parameter(hidden = true) @LoginUser Long userId,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "저장할 관심 카테고리 ID 목록", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryInterestUpdateRequest.class), examples = @ExampleObject(name = "요청 예시", value = CategoryApiExamples.EXAMPLE_UPDATE_INTERESTS_REQUEST))) @RequestBody @Valid CategoryInterestUpdateRequest request) {
                CategoryInterestCommand command = new CategoryInterestCommand(userId, request.categoryIds());
                userBillInterestAppService.updateInterests(command);

                return ResponseEntity.ok().build();
        }
}

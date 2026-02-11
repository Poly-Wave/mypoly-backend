package com.polywave.userservice.api.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsAgreementRequest;
import com.polywave.userservice.api.dto.UserAgreementRequest;
import com.polywave.userservice.application.userterms.command.TermsAgreement;
import com.polywave.userservice.application.userterms.command.UserAgreementCommand;
import com.polywave.userservice.application.userterms.command.service.UserTermsCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserTerms", description = "유저 약관 동의(업서트) API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-terms")
public class UserTermsController {

    private static final String EXAMPLE_AGREE_REQUEST = """
            {
              "termAgreements": [
                { "termId": 1, "agreed": true },
                { "termId": 2, "agreed": false }
              ]
            }
            """;

    private static final String EXAMPLE_CREATED_OK = """
            {
              "success": true,
              "message": "약관 동의 저장 성공",
              "data": null
            }
            """;

    private static final String EXAMPLE_BAD_REQUEST_VALIDATION = """
            {
              "success": false,
              "message": "must not be empty",
              "data": null
            }
            """;

    private static final String EXAMPLE_BAD_REQUEST_DUPLICATE_TERM_ID = """
            {
              "success": false,
              "message": "중복된 약관 ID(termId)가 포함되어 있습니다: [1]",
              "data": null
            }
            """;

    private static final String EXAMPLE_BAD_REQUEST_INVALID_TERM_ID = """
            {
              "success": false,
              "message": "유효하지 않은 약관 ID가 포함되어 있습니다: [999]",
              "data": null
            }
            """;

    private static final String EXAMPLE_UNAUTHORIZED = """
            {
              "success": false,
              "message": "인증이 필요합니다.",
              "data": null
            }
            """;

    private static final String EXAMPLE_FORBIDDEN = """
            {
              "success": false,
              "message": "접근 권한이 없습니다.",
              "data": null
            }
            """;

    private static final String EXAMPLE_USER_NOT_FOUND = """
            {
              "success": false,
              "message": "유저를 찾을 수 없습니다. userId=123",
              "data": null
            }
            """;

    private static final String EXAMPLE_INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "message": "서버 오류가 발생했습니다.",
              "data": null
            }
            """;

    private final UserTermsCommandService userTermsCommandService;

    @Operation(
            summary = "약관 동의 저장(업서트)",
            description = """
                    로그인 사용자의 약관 동의 정보를 저장합니다. (JWT 인증 필요)

                    동작 규칙(업서트)
                    - (user_id, terms_id) 기준으로 **있으면 update**, 없으면 **insert**
                    - 요청에 포함되지 않은 다른 약관 동의 내역은 **삭제하지 않습니다**
                    - 요청 본문에 **중복 termId가 있으면 400** 처리합니다. (DB 유니크 충돌 방지)

                    예외 정책(실제 코드 기준)
                    - 요청 값 검증 실패: 400
                    - 유효하지 않은 약관 ID 포함: 400
                    - 사용자 없음: 404
                    - 인증 필요: 401 / 권한 없음: 403
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = EXAMPLE_CREATED_OK)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패 또는 잘못된 termId/중복 termId",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "검증 실패 예시", value = EXAMPLE_BAD_REQUEST_VALIDATION),
                                    @ExampleObject(name = "중복 termId 예시", value = EXAMPLE_BAD_REQUEST_DUPLICATE_TERM_ID),
                                    @ExampleObject(name = "유효하지 않은 termId 예시", value = EXAMPLE_BAD_REQUEST_INVALID_TERM_ID)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 필요(JWT 누락/만료/위조)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "401 예시", value = EXAMPLE_UNAUTHORIZED)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "403 예시", value = EXAMPLE_FORBIDDEN)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "404 예시", value = EXAMPLE_USER_NOT_FOUND)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "서버 오류 예시", value = EXAMPLE_INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @PostMapping("/agree")
    public ResponseEntity<ApiResponse<Void>> agreeToTerms(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "약관 동의 목록(업서트 대상)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserAgreementRequest.class),
                            examples = @ExampleObject(name = "요청 예시", value = EXAMPLE_AGREE_REQUEST)
                    )
            )
            @Valid @RequestBody UserAgreementRequest request,
            @Parameter(hidden = true) @LoginUser Long userId
    ) {
        // 중복 termId 방지 (DB 유니크 충돌 방지)
        List<Long> duplicates = findDuplicateTermIds(request.termAgreements());
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("중복된 약관 ID(termId)가 포함되어 있습니다: " + duplicates);
        }

        List<TermsAgreement> termAgreements = request.termAgreements().stream()
                .map(ta -> new TermsAgreement(ta.termId(), ta.agreed()))
                .toList();

        UserAgreementCommand command = new UserAgreementCommand(userId, termAgreements);
        userTermsCommandService.saveUserAgreement(command);

        return ResponseEntity.status(CREATED)
                .body(ApiResponse.ok("약관 동의 저장 성공"));
    }

    private List<Long> findDuplicateTermIds(List<TermsAgreementRequest> agreements) {
        Set<Long> seen = new HashSet<>();
        List<Long> duplicates = new ArrayList<>();

        for (TermsAgreementRequest a : agreements) {
            Long termId = a.termId(); // @Valid/@NotNull로 null은 여기까지 안 오는 게 정상
            if (!seen.add(termId)) {
                duplicates.add(termId);
            }
        }

        // 중복 목록은 중복 제거 + 정렬해서 메시지 안정화
        return duplicates.stream().distinct().sorted().toList();
    }
}

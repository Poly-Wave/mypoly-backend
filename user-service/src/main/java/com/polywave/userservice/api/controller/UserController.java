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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Tag(name = "Nickname", description = "닉네임(가용성 검사/랜덤 생성/설정) API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String EXAMPLE_NICKNAME_AVAILABILITY_OK = """
            {
              "success": true,
              "message": "닉네임 사용 가능 여부 조회 성공",
              "data": {
                "available": true
              }
            }
            """;

    private static final String EXAMPLE_RANDOM_NICKNAME_OK = """
            {
              "success": true,
              "message": "랜덤 닉네임 생성 성공",
              "data": {
                "nickname": "파란당근도사"
              }
            }
            """;

    private static final String EXAMPLE_ASSIGN_NICKNAME_REQUEST = """
            {
              "nickname": "당근도사"
            }
            """;

    private static final String EXAMPLE_ASSIGN_NICKNAME_OK = """
            {
              "success": true,
              "message": "닉네임 설정 성공",
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
              "message": "사용자를 찾을 수 없습니다.",
              "data": null
            }
            """;

    private static final String EXAMPLE_FORBIDDEN_NICKNAME = """
            {
              "success": false,
              "message": "금칙어가 포함된 닉네임입니다.",
              "data": null
            }
            """;

    private static final String EXAMPLE_DUPLICATE_NICKNAME = """
            {
              "success": false,
              "message": "이미 사용 중인 닉네임입니다.",
              "data": null
            }
            """;

    private static final String EXAMPLE_VALIDATION_ERROR = """
            {
              "success": false,
              "message": "닉네임은 한글/숫자만 사용 가능하며, 공백은 단어 사이에 1개만 허용됩니다.",
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

    private final NicknameCommandService nicknameCommandService;
    private final NicknameQueryService nicknameQueryService;

    @Operation(
            summary = "닉네임 사용 가능 여부 조회",
            description = """
                    쿼리 파라미터 `nickname`으로 닉네임 사용 가능 여부를 반환합니다.

                    인증
                    - JWT 인증이 필요합니다.
                    - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = EXAMPLE_NICKNAME_AVAILABILITY_OK)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "검증 실패 예시", value = EXAMPLE_VALIDATION_ERROR)
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
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "서버 오류 예시", value = EXAMPLE_INTERNAL_SERVER_ERROR)
                    )
            )
    })
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

    @Operation(
            summary = "랜덤 닉네임 생성",
            description = """
                    서버에서 랜덤 닉네임을 생성해 반환합니다.

                    인증
                    - JWT 인증이 필요합니다.
                    - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = EXAMPLE_RANDOM_NICKNAME_OK)
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
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "서버 오류 예시", value = EXAMPLE_INTERNAL_SERVER_ERROR)
                    )
            )
    })
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

    @Operation(
            summary = "내 닉네임 설정",
            description = """
                    로그인 사용자의 닉네임을 설정(또는 변경)합니다.

                    예외 정책
                    - 요청 값 검증 실패: 400
                    - 금칙어 포함: 400
                    - 중복 닉네임: 409
                    - 사용자 없음: 404

                    인증
                    - JWT 인증이 필요합니다.
                    - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "설정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 응답 예시", value = EXAMPLE_ASSIGN_NICKNAME_OK)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패 또는 금칙어 포함",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "검증 실패 예시", value = EXAMPLE_VALIDATION_ERROR),
                                    @ExampleObject(name = "금칙어 포함 예시", value = EXAMPLE_FORBIDDEN_NICKNAME)
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
                    responseCode = "409",
                    description = "이미 사용 중인 닉네임",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "중복 닉네임 예시", value = EXAMPLE_DUPLICATE_NICKNAME)
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
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> assignNickname(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "설정할 닉네임",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NicknameCreateRequest.class),
                            examples = @ExampleObject(name = "요청 예시", value = EXAMPLE_ASSIGN_NICKNAME_REQUEST)
                    )
            )
            @Valid @RequestBody NicknameCreateRequest request,
            @Parameter(hidden = true) @LoginUser Long userId
    ) {
        nicknameCommandService.assignNickname(userId, request.nickname());
        return ResponseEntity.ok(ApiResponse.ok("닉네임 설정 성공"));
    }
}

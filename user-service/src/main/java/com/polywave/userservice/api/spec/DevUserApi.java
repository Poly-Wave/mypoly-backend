package com.polywave.userservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.userservice.api.example.UserApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

@Tag(name = "User", description = "사용자 정보(닉네임/프로필/주소) 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface DevUserApi {

    @Operation(summary = "[DEV/LOCAL] 내 계정 탈퇴", description = """
            개발/테스트 환경에서만 임시로 사용하는 회원 탈퇴 API입니다.
            - `user.dev-delete.enabled=true` 일 때만 활성화됩니다.
            - JWT로 로그인한 본인 계정을 삭제합니다.
            - Swagger 우측 상단 Authorize에 `Bearer {jwt}` 입력 후 호출하세요.

            주의
            - user-service 내부의 사용자 데이터만 삭제합니다.
            - 다른 서비스(bill-service 등)에 남아 있는 userId 연관 데이터는 별도 정리가 필요할 수 있습니다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "탈퇴 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 필요(JWT 누락/만료/위조)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 필요",
                                    value = CommonApiExamples.EXAMPLE_UNAUTHORIZED
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자를 찾을 수 없음",
                                    value = UserApiExamples.EXAMPLE_USER_NOT_FOUND
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR
                            )
                    )
            )
    })
    @DeleteMapping("/me")
    ResponseEntity<Void> deleteMe(@Parameter(hidden = true) Long userId);
}
package com.polywave.notificationservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Internal User Notification", description = """
        [Internal] 회원 탈퇴 시 사용자 알림 삭제 API.

        `X-Admin-Api-Key` 헤더가 서버 설정값과 일치해야 호출 가능합니다.
        - 운영 배포 시 `NOTIFICATION_ADMIN_API_KEY` 환경변수로 주입
        - 키가 설정되지 않은 환경에서는 무조건 403 (fail-closed)
        """)
@SecurityRequirement(name = "adminApiKey")
@RequestMapping("/internal/user-notifications")
public interface InternalUserNotificationApi {

    @Operation(summary = "[Internal] 사용자 알림 전체 삭제", description = """
            회원 탈퇴 시 해당 사용자의 알림을 모두 물리 삭제한다. 멱등하게 동작한다(없어도 204).
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUserNotifications(
            @Parameter(description = "탈퇴 사용자 ID") @PathVariable Long userId
    );
}

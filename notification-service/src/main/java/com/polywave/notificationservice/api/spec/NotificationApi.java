package com.polywave.notificationservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.notificationservice.api.dto.MyNotificationListResponse;
import com.polywave.notificationservice.api.dto.UnreadNotificationCountResponse;
import com.polywave.notificationservice.api.example.NotificationApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notification", description = "앱 사용자용 내 알림함 API")
@SecurityRequirement(name = "bearerAuth")
// 외부 prefix /notifications 는 server.servlet.context-path 가 담당 (user/bill 서비스 컨벤션).
@RequestMapping
public interface NotificationApi {

    @Operation(summary = "내 알림 목록 조회", description = """
            로그인 사용자의 알림함 목록을 최신순으로 반환합니다.

            - 알림이 없으면 빈 배열을 반환합니다. (404 가 아닙니다)
            - 본문(body)은 피그마 "카테고리 / 본문" 구조의 본문에 해당합니다.
            - 표시 일자(displayDate)는 KST 기준 YYYY.MM.DD 입니다.
            - 페이지네이션은 hasNext 기반입니다.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MyNotificationListResponse.class),
                            examples = {
                                    @ExampleObject(name = "알림 있음", value = NotificationApiExamples.EXAMPLE_MY_NOTIFICATION_LIST_OK),
                                    @ExampleObject(name = "알림 없음", value = NotificationApiExamples.EXAMPLE_MY_NOTIFICATION_LIST_EMPTY)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                    )
            )
    })
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MyNotificationListResponse> getMyNotifications(
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "안 읽은 알림 개수 조회", description = "로그인 사용자의 안 읽은 알림 개수를 반환합니다. (배지 표시용)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UnreadNotificationCountResponse.class),
                            examples = @ExampleObject(name = "조회 성공", value = NotificationApiExamples.EXAMPLE_UNREAD_NOTIFICATION_COUNT_OK)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                    )
            )
    })
    @GetMapping(value = "/me/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "알림 단건 읽음 처리", description = "지정된 알림을 읽음 처리합니다. 이미 읽음 상태이면 멱등하게 200 을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림 없음(타 사용자 알림 포함)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "알림 없음", value = NotificationApiExamples.EXAMPLE_USER_NOTIFICATION_NOT_FOUND)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                    )
            )
    })
    @PatchMapping("/{notificationId}/read")
    ResponseEntity<Void> markAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long notificationId,
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "전체 알림 읽음 처리", description = "현재 사용자의 안 읽은 알림을 모두 읽음 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                    )
            )
    })
    @PatchMapping("/me/read-all")
    ResponseEntity<Void> markAllAsRead(
            @Parameter(hidden = true) Long userId
    );
}

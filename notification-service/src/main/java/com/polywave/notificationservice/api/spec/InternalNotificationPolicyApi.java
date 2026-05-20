package com.polywave.notificationservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.notificationservice.api.dto.NotificationPolicyListResponse;
import com.polywave.notificationservice.api.dto.NotificationPolicyRequest;
import com.polywave.notificationservice.api.dto.NotificationPolicyResponse;
import com.polywave.notificationservice.api.dto.NotificationPolicyStatusUpdateRequest;
import com.polywave.notificationservice.api.example.NotificationApiExamples;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Internal Notification Policy", description = """
        관리자/내부용 알림 정책 관리 API.

        모든 엔드포인트는 `X-Admin-Api-Key` 헤더가 서버 설정값과 일치해야 호출 가능합니다.
        - local 기본값: `local-admin-key`
        - 운영 배포 시 `NOTIFICATION_ADMIN_API_KEY` 환경변수로 주입
        - 키가 설정되지 않은 환경에서는 무조건 403 (fail-closed)
        """)
@SecurityRequirement(name = "adminApiKey")
@RequestMapping("/internal/notification-policies")
public interface InternalNotificationPolicyApi {

    @Operation(summary = "알림 정책 목록 조회", description = """
            관리자/내부용 알림 정책 목록을 조회합니다.

            - 상태 / 채널 / 카테고리로 필터링 가능합니다.
            - 정렬은 정책 ID 내림차순 고정입니다.
            - 페이지네이션은 기존 SliceResponse 패턴과 동일하게 hasNext 기반입니다.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyListResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "서버 오류", value = CommonApiExamples.EXAMPLE_INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NotificationPolicyListResponse> getPolicies(
            @Parameter(description = "운영 상태 필터") @RequestParam(required = false) NotificationPolicyStatus status,
            @Parameter(description = "알림 채널 필터") @RequestParam(required = false) NotificationChannel channel,
            @Parameter(description = "알림 카테고리 필터") @RequestParam(required = false) NotificationCategory category,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "알림 정책 상세 조회", description = "policyId 로 단일 알림 정책을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "정책 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "정책 없음", value = NotificationApiExamples.EXAMPLE_NOTIFICATION_POLICY_NOT_FOUND)
                    )
            )
    })
    @GetMapping(value = "/{policyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NotificationPolicyResponse> getPolicy(
            @Parameter(description = "알림 정책 ID") @PathVariable Long policyId
    );

    @Operation(summary = "알림 정책 생성", description = """
            알림/푸시 정책을 생성합니다.

            - 생성 직후 status 는 READY 로 시작합니다. 실제 발송하려면 별도 상태 변경 API 로 ACTIVE 로 전환해야 합니다.
            - landingType=EXTERNAL_URL 일 때는 landingUrl 이 필수입니다.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값 불량",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "정책 유효성 오류", value = NotificationApiExamples.EXAMPLE_INVALID_NOTIFICATION_POLICY)
                            }
                    )
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NotificationPolicyResponse> createPolicy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 알림 정책",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyRequest.class),
                            examples = @ExampleObject(name = "요청 예시", value = NotificationApiExamples.EXAMPLE_NOTIFICATION_POLICY_CREATE_REQUEST)
                    )
            )
            @RequestBody @Valid NotificationPolicyRequest request
    );

    @Operation(summary = "알림 정책 수정", description = "이미 생성된 알림 정책을 수정합니다. 상태값은 별도 상태 변경 API 로 처리합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "정책 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "정책 없음", value = NotificationApiExamples.EXAMPLE_NOTIFICATION_POLICY_NOT_FOUND)
                    )
            )
    })
    @PatchMapping(value = "/{policyId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NotificationPolicyResponse> updatePolicy(
            @Parameter(description = "알림 정책 ID") @PathVariable Long policyId,
            @RequestBody @Valid NotificationPolicyRequest request
    );

    @Operation(summary = "알림 정책 상태 변경", description = "READY/ACTIVE/INACTIVE 로 상태를 전환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상태 변경 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "정책 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "정책 없음", value = NotificationApiExamples.EXAMPLE_NOTIFICATION_POLICY_NOT_FOUND)
                    )
            )
    })
    @PatchMapping(value = "/{policyId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NotificationPolicyResponse> updatePolicyStatus(
            @Parameter(description = "알림 정책 ID") @PathVariable Long policyId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 상태",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPolicyStatusUpdateRequest.class),
                            examples = @ExampleObject(name = "요청 예시", value = NotificationApiExamples.EXAMPLE_NOTIFICATION_POLICY_STATUS_REQUEST)
                    )
            )
            @RequestBody @Valid NotificationPolicyStatusUpdateRequest request
    );
}

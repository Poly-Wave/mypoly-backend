package com.polywave.notificationservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.notificationservice.api.dto.DevDeliverNotificationRequest;
import com.polywave.notificationservice.api.dto.DevDeliverNotificationResponse;
import com.polywave.notificationservice.api.dto.RunBillStageChangeResponse;
import com.polywave.notificationservice.api.dto.RunBookmarkNoVoteRemindResponse;
import com.polywave.notificationservice.api.dto.RunDailyHomeBroadcastResponse;
import com.polywave.notificationservice.api.dto.RunDailyInterestAgendaResponse;
import com.polywave.notificationservice.api.dto.RunNoticeBroadcastResponse;
import com.polywave.notificationservice.api.dto.RunOnboardingRemindResponse;
import com.polywave.notificationservice.api.example.NotificationApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Dev Notification", description = """
        [DEV/LOCAL 전용] Swagger 만으로 알림 발급 → 알림함 조회까지 end-to-end 테스트하기 위한 엔드포인트.

        - `notification.dev-trigger.enabled=true` 인 환경에서만 컨트롤러가 등록됩니다.
        - 운영에서는 절대 활성화하지 마세요.
        """)
@RequestMapping("/dev-notifications")
public interface DevNotificationApi {

    @Operation(summary = "[DEV] 알림 강제 발급", description = """
            지정된 policyId 기준으로 userId 에게 알림을 1건 발급합니다.

            - 정책의 status 가 ACTIVE 가 아니면 skip (응답 delivered=false).
            - 동일 (userId, dedupKey) 로 재호출 시 멱등 skip.
            - PUSH 채널이면 NoopPushSender 가 로그만 남깁니다.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "처리 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DevDeliverNotificationResponse.class)
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
    @PostMapping(value = "/deliver", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<DevDeliverNotificationResponse> deliver(
            @RequestBody @Valid DevDeliverNotificationRequest request
    );

    @Operation(summary = "[DEV] 온보딩 리마인더 스케줄러 수동 실행", description = """
            노션 행 1(별명 설정 D+1) + 행 2(관심 주제 선택 D+1) 스케줄러 로직을 즉시 1회 실행합니다.

            - 정책이 시드되어 있지 않거나 status != ACTIVE 면 자동 skip 됩니다.
            - 결과로 각 행에서 신규 발급된 건수를 반환합니다.
            - dedupKey = "{POLICY_KEY}:{userId}" 라 동일 유저에게 평생 1회만 발급됩니다.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RunOnboardingRemindResponse.class)
                    )
            )
    })
    @PostMapping(value = "/run-onboarding-remind", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RunOnboardingRemindResponse> runOnboardingRemind();

    @Operation(summary = "[DEV] 매일 홈 broadcast 스케줄러 수동 실행", description = """
            노션 행 4(요즘 핫한 안건) + 행 5(최근 30일 인기) 스케줄러 로직을 즉시 1회 실행합니다.

            - 대상: 추가 정보 입력까지 완료한 유저 전원 (profile_completed_at != null).
            - dedupKey = "{POLICY_KEY}:{userId}:{date(KST)}" → 같은 날 중복 발급 방지.
            - 정책이 ACTIVE 가 아니면 자동 skip.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RunDailyHomeBroadcastResponse.class)
                    )
            )
    })
    @PostMapping(value = "/run-daily-home-broadcast", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RunDailyHomeBroadcastResponse> runDailyHomeBroadcast();

    @Operation(summary = "[DEV] 북마크 D+1 미투표 스케줄러 수동 실행", description = """
            노션 행 7 스케줄러 로직을 즉시 1회 실행합니다.

            - bill-service /internal/segments/bookmarked-unvoted 호출.
            - dedupKey = "{POLICY_KEY}:{userId}:{billId}" → 한 의안 한 유저에게 평생 1회.
            - 정책이 ACTIVE 가 아니면 자동 skip.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RunBookmarkNoVoteRemindResponse.class)
                    )
            )
    })
    @PostMapping(value = "/run-bookmark-no-vote-remind", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RunBookmarkNoVoteRemindResponse> runBookmarkNoVoteRemind();

    @Operation(summary = "[DEV] 관심 카테고리 신규 안건 스케줄러 수동 실행", description = """
            노션 행 3 스케줄러 로직을 즉시 1회 실행합니다.

            - 어제 KST 등록된 안건 중 사용자의 관심 카테고리에 매칭되는 카운트.
            - dedupKey = "{POLICY_KEY}:{userId}:{date(KST)}" → 같은 날 중복 발급 방지.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RunDailyInterestAgendaResponse.class)
                    )
            )
    })
    @PostMapping(value = "/run-daily-interest-agenda", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RunDailyInterestAgendaResponse> runDailyInterestAgenda();

    @Operation(summary = "[DEV] 북마크 단계 변경 알림 polling 수동 실행", description = """
            노션 행 6 (북마크 의안 단계 변경) polling 로직을 즉시 1회 실행합니다.

            - bill-service /internal/segments/bookmarked-stage-changes (since = now - 1h) 호출.
            - dedupKey = "{POLICY_KEY}:{userId}:{billId}:{toStageCode}" → 한 단계 전이당 1회.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RunBillStageChangeResponse.class)
                    )
            )
    })
    @PostMapping(value = "/run-bill-stage-change", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RunBillStageChangeResponse> runBillStageChange();

    @Operation(summary = "[DEV] 공지사항 broadcast 스케줄러 수동 실행", description = """
            notices 테이블에서 is_visible=true 이고 broadcast_at 이 비어있는 공지사항을 찾아
            전체 유저(관심 주제 선택 완료)에게 알림함 항목을 즉시 1회 발급합니다.

            - NOTICE_PUBLISHED_BROADCAST 정책이 ACTIVE 가 아니면 자동 skip (broadcast_at 은 채워지지 않고 재시도 대상으로 남음).
            - dedupKey = "NOTICE_PUBLISHED_BROADCAST:{noticeId}:{userId}" → 공지 1건당 유저 1회만 발급.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RunNoticeBroadcastResponse.class)
                    )
            )
    })
    @PostMapping(value = "/run-notice-broadcast", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RunNoticeBroadcastResponse> runNoticeBroadcast();
}

package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.policy.command.NotificationPolicyCommand;
import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알림 정책 생성/수정 요청")
public record NotificationPolicyRequest(
        @Schema(description = "정책 식별 키(선택, 시스템 발송 매핑용)", example = "ONBOARDING_INTEREST_REMIND_D1")
        String policyKey,

        @Schema(description = "정책 이름", example = "별명 설정 완료 + 관심 주제 미선택 유저", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name,

        @Schema(description = "노출 위치(노션 Depth 라벨)", example = "온보딩")
        String depth,

        @Schema(description = "알림 채널", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"IN_APP", "PUSH"})
        @NotNull
        NotificationChannel channel,

        @Schema(description = "알림 카테고리", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"NOTICE", "BILL", "SUBSIDY", "ETC"})
        @NotNull
        NotificationCategory category,

        @Schema(description = "발송 대상(노션 발송 대상 자유 텍스트)", example = "별명 설정 완료 + 관심 주제 미선택 유저")
        String targetAudience,

        @Schema(description = "발송 시점(노션 발송 시점 자유 텍스트)", example = "별명 설정 완료 D+1일 12:00")
        String sendSchedule,

        @Schema(description = "메시지 타이틀(푸시 헤드라인, 앱 알림함에서는 선택값)", example = "AI가 관심있는 안건만 모아드려요.")
        String title,

        @Schema(description = "메시지 본문(앱 알림함 표시 본문)", example = "관심 주제를 선택하면 {별명}님에게 필요한 안건만 모아볼 수 있어요.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String body,

        @Schema(description = "랜딩 타입", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"NOTICE_DETAIL", "BILL_DETAIL", "SUBSIDY_DETAIL", "NOTIFICATION_LIST", "EXTERNAL_URL", "NONE"})
        @NotNull
        LandingType landingType,

        @Schema(description = "외부 URL 랜딩일 때 사용", example = "")
        String landingUrl
) {
    public NotificationPolicyCommand toCommand() {
        return new NotificationPolicyCommand(
                policyKey,
                name,
                depth,
                channel,
                category,
                targetAudience,
                sendSchedule,
                title,
                body,
                landingType,
                landingUrl
        );
    }
}

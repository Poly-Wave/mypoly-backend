package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notification.user.query.result.UserNotificationResult;
import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Schema(description = "앱 알림함 단건 응답")
public record MyNotificationResponse(
        @Schema(description = "알림 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long notificationId,

        @Schema(description = "알림 카테고리", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"NOTICE", "BILL", "SUBSIDY", "ETC"})
        NotificationCategory category,

        @Schema(description = "알림 카테고리 표시명", example = "공지사항", requiredMode = Schema.RequiredMode.REQUIRED)
        String categoryName,

        @Schema(description = "알림 타이틀(앱 알림함에서는 비어있을 수 있음, 빈 문자열 방어)", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "알림 본문(피그마 알림 리스트의 본문)", example = "북마크한 보조금 신청기간입니다", requiredMode = Schema.RequiredMode.REQUIRED)
        String body,

        @Schema(description = "발송 시각(KST 오프셋)", example = "2026-05-20T12:30:00+09:00", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime sentAt,

        @Schema(description = "앱 표시 일자(KST, YYYY.MM.DD)", example = "2026.05.20", requiredMode = Schema.RequiredMode.REQUIRED)
        String displayDate,

        @Schema(description = "읽음 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isRead,

        @Schema(description = "랜딩 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        LandingType landingType,

        @Schema(description = "랜딩 대상 ID(상세 화면 이동 시 사용)", example = "10")
        Long landingId,

        @Schema(description = "외부 URL(없으면 빈 문자열)", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
        String landingUrl
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static MyNotificationResponse from(UserNotificationResult r) {
        Instant sentAt = r.sentAt();
        return new MyNotificationResponse(
                r.notificationId(),
                r.category(),
                nullToEmpty(r.categoryName()),
                nullToEmpty(r.title()),
                nullToEmpty(r.body()),
                sentAt == null ? null : sentAt.atZone(KST).toOffsetDateTime(),
                sentAt == null ? "" : LocalDate.ofInstant(sentAt, KST).format(DISPLAY_DATE),
                r.isRead(),
                r.landingType() == null ? LandingType.NONE : r.landingType(),
                r.landingId(),
                nullToEmpty(r.landingUrl())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

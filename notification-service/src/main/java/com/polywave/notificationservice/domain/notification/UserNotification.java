package com.polywave.notificationservice.domain.notification;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 알림 이력. 알림 정책 1건이 N 명에게 발송될 수 있으므로
 * 정책(NotificationPolicy)과 분리하여 사용자 단위로 보관한다.
 *
 * - 채널과 무관하게 앱 알림함에 노출되는 이력으로 관리한다.
 *   (PUSH 발송 알림도 동일하게 알림함에 쌓인다)
 * - 정책 본문/타이틀을 발송 시점에 스냅샷으로 저장하여
 *   정책이 사후에 수정/삭제되어도 알림함 표시가 깨지지 않도록 한다.
 * - dedupKey 로 동일 배치/이벤트 재실행 시 중복 생성을 방지한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_notifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_notifications_user_dedup",
                        columnNames = {"user_id", "dedup_key"}
                )
        }
        // 인덱스는 V7 마이그레이션에서 직접 생성한다.
        // Hibernate @Index columnList 는 BaseEntity 의 createdAt 같은 매핑 컬럼을
        // 안정적으로 해석하지 못하는 경우가 있어 검증 단계에서 누락 에러가 발생한다.
)
public class UserNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "policy_id")
    private Long policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private NotificationCategory category;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "landing_type", nullable = false, length = 30)
    private LandingType landingType;

    @Column(name = "landing_id")
    private Long landingId;

    @Column(name = "landing_url", length = 1000)
    private String landingUrl;

    @Column(name = "dedup_key", length = 200)
    private String dedupKey;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    private UserNotification(
            Long userId,
            Long policyId,
            NotificationChannel channel,
            NotificationCategory category,
            String title,
            String body,
            LandingType landingType,
            Long landingId,
            String landingUrl,
            String dedupKey,
            Instant sentAt
    ) {
        this.userId = userId;
        this.policyId = policyId;
        this.channel = channel;
        this.category = category;
        this.title = title;
        this.body = body;
        this.landingType = landingType;
        this.landingId = landingId;
        this.landingUrl = landingUrl;
        this.dedupKey = dedupKey;
        this.sentAt = sentAt;
    }

    public static UserNotification create(
            Long userId,
            Long policyId,
            NotificationChannel channel,
            NotificationCategory category,
            String title,
            String body,
            LandingType landingType,
            Long landingId,
            String landingUrl,
            String dedupKey,
            Instant sentAt
    ) {
        return new UserNotification(
                userId,
                policyId,
                channel,
                category,
                title,
                body,
                landingType == null ? LandingType.NONE : landingType,
                landingId,
                landingUrl,
                dedupKey,
                sentAt == null ? Instant.now() : sentAt
        );
    }

    public boolean isRead() {
        return readAt != null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void markAsRead(Instant readAt) {
        if (this.readAt == null) {
            this.readAt = readAt == null ? Instant.now() : readAt;
        }
    }

    public void softDelete(Instant deletedAt) {
        if (this.deletedAt == null) {
            this.deletedAt = deletedAt == null ? Instant.now() : deletedAt;
        }
    }
}

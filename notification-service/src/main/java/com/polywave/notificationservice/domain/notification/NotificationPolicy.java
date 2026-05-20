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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림/푸시 정책(노션 "알림/푸시 리스트" 한 행에 해당).
 *
 * - 정책 자체는 "어떤 알림을 누구에게 언제 어떤 메시지로 보낼지" 의 메타이고,
 *   실제 사용자에게 생성/노출된 이력은 {@link UserNotification} 으로 분리한다.
 * - depth 는 노션의 "온보딩 / 홈 / 안건" 같은 노출 위치 라벨이며 단순 문자열로 둔다.
 *   (추후 enum 으로 분리해도 무방하나 운영성/확장성 위해 현재는 문자열 유지)
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_policies")
public class NotificationPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_key", length = 100, unique = true)
    private String policyKey;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "depth", length = 50)
    private String depth;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private NotificationCategory category;

    @Column(name = "target_audience", length = 500)
    private String targetAudience;

    @Column(name = "send_schedule", length = 500)
    private String sendSchedule;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "landing_type", nullable = false, length = 30)
    private LandingType landingType;

    @Column(name = "landing_url", length = 1000)
    private String landingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationPolicyStatus status;

    public void update(
            String name,
            String depth,
            NotificationChannel channel,
            NotificationCategory category,
            String targetAudience,
            String sendSchedule,
            String title,
            String body,
            LandingType landingType,
            String landingUrl
    ) {
        this.name = name;
        this.depth = depth;
        this.channel = channel;
        this.category = category;
        this.targetAudience = targetAudience;
        this.sendSchedule = sendSchedule;
        this.title = title;
        this.body = body;
        this.landingType = landingType;
        this.landingUrl = landingUrl;
    }

    public void changeStatus(NotificationPolicyStatus status) {
        this.status = status;
    }
}

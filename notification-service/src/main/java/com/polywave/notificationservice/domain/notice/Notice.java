package com.polywave.notificationservice.domain.notice;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항. admin-tool 범용 CRUD로 title/content/isVisible 을 직접 등록/수정하므로
 * 애플리케이션 코드에는 생성자/setter 를 두지 않는다.
 *
 * broadcastAt 은 {@code NoticeBroadcastScheduler} 가 이 공지를 전체 유저에게
 * 알림함(UserNotification) 으로 발급 완료한 시각을 기록한다 (null 이면 아직 미발급).
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notices")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;

    @Column(name = "broadcast_at")
    private Instant broadcastAt;

    public void markBroadcasted(Instant broadcastAt) {
        this.broadcastAt = broadcastAt == null ? Instant.now() : broadcastAt;
    }
}

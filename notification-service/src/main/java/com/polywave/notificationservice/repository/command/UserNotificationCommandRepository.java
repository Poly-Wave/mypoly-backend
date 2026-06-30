package com.polywave.notificationservice.repository.command;

import com.polywave.notificationservice.domain.notification.UserNotification;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserNotificationCommandRepository extends JpaRepository<UserNotification, Long> {

    Optional<UserNotification> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    boolean existsByUserIdAndDedupKey(Long userId, String dedupKey);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE UserNotification n
               SET n.readAt = :readAt,
                   n.updatedAt = :readAt
             WHERE n.userId = :userId
               AND n.readAt IS NULL
               AND n.deletedAt IS NULL
            """)
    int markAllAsRead(@Param("userId") Long userId, @Param("readAt") Instant readAt);

    // 회원 탈퇴 시 해당 사용자의 알림을 물리 삭제한다(soft-delete 가 아닌 완전 삭제). 멱등.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserNotification n WHERE n.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

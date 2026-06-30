package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBillViewCommandRepository extends JpaRepository<UserBillView, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO bill_service.user_bill_views (
                user_id,
                bill_id,
                last_viewed_at,
                created_at,
                updated_at
            )
            VALUES (
                :userId,
                :billId,
                now(),
                now(),
                now()
            )
            ON CONFLICT (user_id, bill_id)
            DO UPDATE SET
                last_viewed_at = EXCLUDED.last_viewed_at,
                updated_at = EXCLUDED.updated_at
            WHERE user_bill_views.last_viewed_at <= now() - INTERVAL '24 hours'
            """, nativeQuery = true)
    int upsertIfViewCountable(@Param("userId") Long userId, @Param("billId") Long billId);

    // 회원 탈퇴 시 해당 사용자의 조회 기록 전체 삭제 (멱등).
    @Modifying
    @Query("DELETE FROM UserBillView v WHERE v.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
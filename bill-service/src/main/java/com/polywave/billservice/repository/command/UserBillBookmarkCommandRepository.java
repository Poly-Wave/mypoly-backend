package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBillBookmarkCommandRepository extends JpaRepository<UserBillBookmark, Long> {

    void deleteByUserIdAndBill_Id(Long userId, Long billId);

    // 회원 탈퇴 시 해당 사용자의 북마크 전체 삭제 (멱등: 0건이어도 정상).
    @Modifying
    @Query("DELETE FROM UserBillBookmark b WHERE b.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
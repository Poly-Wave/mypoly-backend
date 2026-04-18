package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBillBookmarkCommandRepository extends JpaRepository<UserBillBookmark, Long> {

    void deleteByUserIdAndBill_Id(Long userId, Long billId);
}
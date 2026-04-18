package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillBookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBillBookmarkCommandRepository extends JpaRepository<UserBillBookmark, Long> {

    boolean existsByUserIdAndBill_Id(Long userId, Long billId);

    Optional<UserBillBookmark> findByUserIdAndBill_Id(Long userId, Long billId);

    void deleteByUserIdAndBill_Id(Long userId, Long billId);
}
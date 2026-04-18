package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillCommandRepository extends JpaRepository<Bill, Long> {

    @Modifying
    @Query("""
            UPDATE Bill bill
            SET bill.viewCount = bill.viewCount + 1
            WHERE bill.id = :billId
            """)
    int increaseViewCount(@Param("billId") Long billId);
}
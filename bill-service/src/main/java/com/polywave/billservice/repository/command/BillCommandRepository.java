package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillCommandRepository extends JpaRepository<Bill, Long> {
}

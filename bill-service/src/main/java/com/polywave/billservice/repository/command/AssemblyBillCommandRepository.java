package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.AssemblyBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssemblyBillCommandRepository extends JpaRepository<AssemblyBill, Long> {
}

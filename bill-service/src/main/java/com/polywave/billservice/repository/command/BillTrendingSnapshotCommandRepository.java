package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.BillTrendingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillTrendingSnapshotCommandRepository extends JpaRepository<BillTrendingSnapshot, Long> {
}

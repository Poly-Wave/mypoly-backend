package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.BillCategory;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCommandRepository extends JpaRepository<BillCategory, Long> {
    List<BillCategory> findAllActiveByIdIn(Collection<Long> ids);
}

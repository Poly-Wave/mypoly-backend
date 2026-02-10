package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.BillCategory;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCommandRepository extends JpaRepository<BillCategory, Long> {

    /**
     * 관심사 저장 시 사용: 활성화된 카테고리만 조회
     */
    List<BillCategory> findAllByIdInAndIsActiveTrue(Collection<Long> ids);
}

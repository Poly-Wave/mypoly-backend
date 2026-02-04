package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillInterest;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBillInterestCommandRepository extends JpaRepository<UserBillInterest, Long> {

    void deleteByUserIdAndCategoryIdIn(Long userId, Set<Long> categoryIds);
}

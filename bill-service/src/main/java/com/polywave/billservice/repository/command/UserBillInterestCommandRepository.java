package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillInterest;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBillInterestCommandRepository extends JpaRepository<UserBillInterest, Long> {

    /**
     * (주의) UserBillInterest 엔티티에서 category는 연관관계 필드이므로
     * 파생 쿼리 메서드는 category.id 형태로 "category_Id" 를 사용해야 합니다.
     */
    void deleteByUserIdAndCategory_IdIn(Long userId, Set<Long> categoryIds);
}

package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.category.query.result.UserCategoryInterestResult;
import java.util.List;

public interface UserBillInterestQueryRepository {
    List<UserCategoryInterestResult> findCategoryIdsByUserId(Long userId);
}

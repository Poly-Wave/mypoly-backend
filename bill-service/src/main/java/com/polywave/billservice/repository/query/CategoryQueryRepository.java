package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import java.util.List;
import java.util.Set;

public interface CategoryQueryRepository {

    List<CategoryResult> findAllActiveOrderByDisplayOrder();

    Set<Long> findActiveCategoryIdsByCodes(Set<String> categoryCodes);
}

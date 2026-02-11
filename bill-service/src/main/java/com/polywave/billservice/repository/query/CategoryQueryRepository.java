package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import java.util.List;

public interface CategoryQueryRepository {

    List<CategoryResult> findAllActiveOrderByDisplayOrder();
}

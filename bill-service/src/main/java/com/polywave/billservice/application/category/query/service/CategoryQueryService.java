package com.polywave.billservice.application.category.query.service;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.repository.query.CategoryQueryRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {

    private final CategoryQueryRepository categoryQueryRepository;

    public List<CategoryResult> getActiveCategories() {
        return categoryQueryRepository.findAllActiveOrderByDisplayOrder();
    }
}

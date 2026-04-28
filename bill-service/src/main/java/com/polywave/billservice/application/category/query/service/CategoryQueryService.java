package com.polywave.billservice.application.category.query.service;

import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.repository.query.CategoryQueryRepository;
import java.util.List;
import java.util.Set;
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

    /**
     * 카테고리 코드 목록을 활성 카테고리 ID 집합으로 변환
     * 존재하지 않거나 비활성화된 코드는 자동으로 제외됨
     */
    public Set<Long> resolveActiveCategoryIdsByCodes(Set<String> categoryCodes) {
        if (categoryCodes.isEmpty()) {
            return Set.of();
        }
        return categoryQueryRepository.findActiveCategoryIdsByCodes(categoryCodes);
    }
}

package com.polywave.billservice.application.category.query.result;

/* 카테고리 목록 조회 결과 */
public record CategoryResult(
        Long id,
        String code,
        String name,
        Integer displayOrder
) {
}

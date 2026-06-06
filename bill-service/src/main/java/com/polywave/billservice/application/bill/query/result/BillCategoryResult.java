package com.polywave.billservice.application.bill.query.result;

public record BillCategoryResult(
        Long categoryId,
        String categoryCode,
        String categoryName,
        String categoryTextColor,
        Integer rankOrder
) {
}
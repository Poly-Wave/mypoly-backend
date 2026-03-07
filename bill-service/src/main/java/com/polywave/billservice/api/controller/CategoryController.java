package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.CategoryInterestUpdateRequest;
import com.polywave.billservice.api.dto.CategoryResponse;
import com.polywave.billservice.api.spec.CategoryApi;
import com.polywave.billservice.application.category.UserBillInterestAppService;
import com.polywave.billservice.application.category.command.service.CategoryInterestCommand;
import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.application.category.query.service.CategoryQueryService;
import com.polywave.security.annotation.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryQueryService categoryQueryService;
    private final UserBillInterestAppService userBillInterestAppService;

    @Value("${bill.category.icon-base-url}")
    private String iconBaseUrl;

    private static final String ICON_PREFIX = "bill-categories";

    @Override
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResult> results = categoryQueryService.getActiveCategories();

        List<CategoryResponse> categories = results.stream()
                .map(dto -> {
                    String iconUrl = iconBaseUrl + "/" + ICON_PREFIX + "/" + dto.code() + ".webp";
                    return CategoryResponse.from(dto, iconUrl);
                })
                .toList();

        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<Void> updateInterests(
            @LoginUser Long userId,
            CategoryInterestUpdateRequest request) {
        CategoryInterestCommand command = new CategoryInterestCommand(userId, request.categoryIds());
        userBillInterestAppService.updateInterests(command);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateOnboardingInterests(
            @LoginUser Long userId,
            CategoryInterestUpdateRequest request) {
        CategoryInterestCommand command = new CategoryInterestCommand(userId, request.categoryIds());
        userBillInterestAppService.saveOnboardingInterests(command);

        return ResponseEntity.ok().build();
    }
}
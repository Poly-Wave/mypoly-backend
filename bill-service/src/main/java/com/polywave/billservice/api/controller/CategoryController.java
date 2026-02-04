package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.ApiResponse;
import com.polywave.billservice.api.dto.CategoryInterestUpdateRequest;
import com.polywave.billservice.api.dto.CategoryResponse;
import com.polywave.billservice.application.category.UserBillInterestAppService;
import com.polywave.billservice.application.category.command.service.CategoryInterestCommand;
import com.polywave.billservice.application.category.query.result.CategoryResult;
import com.polywave.billservice.application.category.query.service.CategoryQueryService;
import com.polywave.security.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryQueryService categoryQueryService;
    private final UserBillInterestAppService userBillInterestAppService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResult> results = categoryQueryService.getActiveCategories();

        List<CategoryResponse> categories = results.stream()
                .map(CategoryResponse::from)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "카테고리 목록 조회 성공",
                        categories
                )
        );
    }

    @Operation(
            summary = "관심 카테고리 저장",
            description = "사용자의 관심 카테고리 목록을 저장합니다. JWT 인증이 필요합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/interests")
    public ResponseEntity<ApiResponse<Void>> updateInterests(@LoginUser Long userId,
                                                             @RequestBody @Valid CategoryInterestUpdateRequest request) {

        CategoryInterestCommand command =
                new CategoryInterestCommand(userId, request.categoryIds());

        userBillInterestAppService.updateInterests(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("관심 카테고리 저장 성공"));
    }

}

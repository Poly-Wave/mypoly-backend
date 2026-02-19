package com.polywave.billservice.api.example;

public final class CategoryApiExamples {

    private CategoryApiExamples() {}

    public static final String EXAMPLE_GET_CATEGORIES_OK = """
            {
              "success": true,
              "message": "카테고리 목록 조회 성공",
              "data": [
                { "id": 1, "code": "DIGITAL", "name": "디지털" },
                { "id": 2, "code": "SECURITY", "name": "보안" }
              ]
            }
            """;

    public static final String EXAMPLE_UPDATE_INTERESTS_REQUEST = """
            {
              "categoryIds": [1, 2, 3]
            }
            """;

    public static final String EXAMPLE_UPDATE_INTERESTS_OK = """
            {
              "success": true,
              "message": "관심 카테고리 저장 성공",
              "data": null
            }
            """;

    public static final String EXAMPLE_BAD_REQUEST_VALIDATION = """
            {
              "success": false,
              "message": "must not be empty",
              "data": null
            }
            """;
}

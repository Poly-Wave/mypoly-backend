package com.polywave.userservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserTermsApiExamples {

    public static final String EXAMPLE_AGREE_REQUEST = """
            {
              "termAgreements": [
                { "termId": 1, "agreed": true },
                { "termId": 2, "agreed": false }
              ]
            }
            """;

    public static final String EXAMPLE_CREATED_OK = """
            {
              "success": true,
              "message": "약관 동의 저장 성공",
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

    public static final String EXAMPLE_BAD_REQUEST_DUPLICATE_TERM_ID = """
            {
              "success": false,
              "message": "중복된 약관 ID(termId)가 포함되어 있습니다: [1]",
              "data": null
            }
            """;

    public static final String EXAMPLE_BAD_REQUEST_INVALID_TERM_ID = """
            {
              "success": false,
              "message": "유효하지 않은 약관 ID가 포함되어 있습니다: [999]",
              "data": null
            }
            """;

    public static final String EXAMPLE_USER_NOT_FOUND = """
            {
              "success": false,
              "message": "유저를 찾을 수 없습니다. userId=123",
              "data": null
            }
            """;
}

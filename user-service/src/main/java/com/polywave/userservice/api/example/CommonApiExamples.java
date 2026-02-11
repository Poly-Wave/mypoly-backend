package com.polywave.userservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonApiExamples {

    public static final String EXAMPLE_UNAUTHORIZED = """
            {
              "success": false,
              "message": "인증이 필요합니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_FORBIDDEN = """
            {
              "success": false,
              "message": "접근 권한이 없습니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "message": "서버 오류가 발생했습니다.",
              "data": null
            }
            """;
}

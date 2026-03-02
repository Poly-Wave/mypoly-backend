package com.polywave.common.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonApiExamples {

    public static final String EXAMPLE_BAD_REQUEST = """
            {
              "code": "BAD_REQUEST"
            }
            """;

    public static final String EXAMPLE_UNAUTHORIZED = """
            {
              "code": "UNAUTHORIZED"
            }
            """;

    public static final String EXAMPLE_FORBIDDEN = """
            {
              "code": "FORBIDDEN"
            }
            """;

    public static final String EXAMPLE_INTERNAL_SERVER_ERROR = """
            {
              "code": "INTERNAL_SERVER_ERROR"
            }
            """;
}
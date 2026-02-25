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
      {}
      """;

  public static final String EXAMPLE_BAD_REQUEST_VALIDATION = """
      {
        "code": "VALIDATION_ERROR"
      }
      """;

  public static final String EXAMPLE_BAD_REQUEST_DUPLICATE_TERM_ID = """
      {
        "code": "BAD_REQUEST"
      }
      """;

  public static final String EXAMPLE_BAD_REQUEST_INVALID_TERM_ID = """
      {
        "code": "BAD_REQUEST"
      }
      """;

  public static final String EXAMPLE_USER_NOT_FOUND = """
      {
        "code": "USER_NOT_FOUND"
      }
      """;
}

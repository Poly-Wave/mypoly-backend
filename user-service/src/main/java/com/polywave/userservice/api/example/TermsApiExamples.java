package com.polywave.userservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TermsApiExamples {

  public static final String EXAMPLE_TERMS_LIST_OK = """
      {
        "terms": [
          {
            "id": 1,
            "name": "SERVICE_TERMS",
            "title": "서비스 이용약관",
            "version": 1,
            "required": true,
            "effectiveFrom": "2026-01-01"
          }
        ]
      }
      """;

  public static final String EXAMPLE_TERMS_META_OK = """
      {
        "id": 1,
        "name": "SERVICE_TERMS",
        "title": "서비스 이용약관",
        "version": 1,
        "required": true,
        "effectiveFrom": "2026-01-01"
      }
      """;

  public static final String EXAMPLE_TERMS_NOT_FOUND = """
      {
        "code": "TERMS_NOT_FOUND"
      }
      """;
}

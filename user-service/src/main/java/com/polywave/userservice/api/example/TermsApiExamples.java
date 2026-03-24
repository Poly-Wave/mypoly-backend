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
            "name": "TERMS_OF_SERVICE",
            "title": "서비스 이용약관",
            "version": 1,
            "required": true,
            "isMarketing": false,
            "effectiveFrom": "2026-01-01"
          },
          {
            "id": 2,
            "name": "PRIVACY_POLICY",
            "title": "개인정보 처리방침",
            "version": 1,
            "required": true,
            "isMarketing": false,
            "effectiveFrom": "2026-01-01"
          },
          {
            "id": 3,
            "name": "MARKETING_CONSENT",
            "title": "광고성 정보 수신 동의",
            "version": 1,
            "required": false,
            "isMarketing": true,
            "effectiveFrom": "2026-01-01"
          }
        ]
      }
      """;

  public static final String EXAMPLE_TERMS_META_OK = """
      {
        "id": 3,
        "name": "MARKETING_CONSENT",
        "title": "광고성 정보 수신 동의",
        "version": 1,
        "required": false,
        "isMarketing": true,
        "effectiveFrom": "2026-01-01"
      }
      """;

  public static final String EXAMPLE_TERMS_NOT_FOUND = """
      {
        "code": "TERMS_NOT_FOUND"
      }
      """;
}
package com.polywave.userservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TermsApiExamples {

    public static final String EXAMPLE_TERMS_LIST_OK = """
            {
              "success": true,
              "message": "약관 목록 조회 성공",
              "data": {
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
            }
            """;

    public static final String EXAMPLE_TERMS_META_OK = """
            {
              "success": true,
              "message": "약관 메타 조회 성공",
              "data": {
                "id": 1,
                "name": "SERVICE_TERMS",
                "title": "서비스 이용약관",
                "version": 1,
                "required": true,
                "effectiveFrom": "2026-01-01"
              }
            }
            """;

    public static final String EXAMPLE_TERMS_NOT_FOUND = """
            {
              "success": false,
              "message": "약관을 찾을 수 없습니다. termsId=999",
              "data": null
            }
            """;
}

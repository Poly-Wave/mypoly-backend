package com.polywave.notificationservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoticeApiExamples {

    public static final String EXAMPLE_NOTICE_LIST_OK = """
            {
              "notices": [
                {
                  "noticeId": 1,
                  "title": "[공지] 서비스 점검 안내",
                  "displayDate": "2026.12.30"
                }
              ],
              "page": 0,
              "size": 20,
              "hasNext": false
            }
            """;

    public static final String EXAMPLE_NOTICE_LIST_EMPTY = """
            {
              "notices": [],
              "page": 0,
              "size": 20,
              "hasNext": false
            }
            """;

    public static final String EXAMPLE_NOTICE_DETAIL_OK = """
            {
              "noticeId": 1,
              "title": "[공지] 서비스 점검 안내",
              "content": "안녕하세요. MYPOLY 입니다.\\n서비스 점검을 안내드립니다.",
              "displayDate": "2026.12.30"
            }
            """;

    public static final String EXAMPLE_NOTICE_NOT_FOUND = """
            {
              "code": "NOTICE_NOT_FOUND"
            }
            """;
}

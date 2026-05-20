package com.polywave.notificationservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationApiExamples {

    public static final String EXAMPLE_NOTIFICATION_POLICY_CREATE_REQUEST = """
            {
              "policyKey": "ONBOARDING_INTEREST_REMIND_D1",
              "name": "별명 설정 완료 + 관심 주제 미선택 유저",
              "depth": "온보딩",
              "channel": "PUSH",
              "category": "ETC",
              "targetAudience": "별명 설정 완료 + 관심 주제 미선택 유저",
              "sendSchedule": "별명 설정 완료 D+1일 12:00",
              "title": "AI가 관심있는 안건만 모아드려요.",
              "body": "관심 주제를 선택하면 {별명}님에게 필요한 안건만 모아볼 수 있어요.",
              "landingType": "NOTIFICATION_LIST",
              "landingUrl": ""
            }
            """;

    public static final String EXAMPLE_NOTIFICATION_POLICY_STATUS_REQUEST = """
            {
              "status": "ACTIVE"
            }
            """;

    public static final String EXAMPLE_MY_NOTIFICATION_LIST_OK = """
            {
              "notifications": [
                {
                  "notificationId": 1,
                  "category": "SUBSIDY",
                  "categoryName": "보조금",
                  "title": "",
                  "body": "북마크한 보조금 신청기간입니다",
                  "sentAt": "2026-05-20T12:30:00+09:00",
                  "displayDate": "2026.05.20",
                  "isRead": false,
                  "landingType": "SUBSIDY_DETAIL",
                  "landingId": 10,
                  "landingUrl": ""
                }
              ],
              "page": 0,
              "size": 20,
              "hasNext": false
            }
            """;

    public static final String EXAMPLE_MY_NOTIFICATION_LIST_EMPTY = """
            {
              "notifications": [],
              "page": 0,
              "size": 20,
              "hasNext": false
            }
            """;

    public static final String EXAMPLE_UNREAD_NOTIFICATION_COUNT_OK = """
            {
              "unreadCount": 3
            }
            """;

    public static final String EXAMPLE_NOTIFICATION_POLICY_NOT_FOUND = """
            {
              "code": "NOTIFICATION_POLICY_NOT_FOUND"
            }
            """;

    public static final String EXAMPLE_USER_NOTIFICATION_NOT_FOUND = """
            {
              "code": "USER_NOTIFICATION_NOT_FOUND"
            }
            """;

    public static final String EXAMPLE_INVALID_NOTIFICATION_POLICY = """
            {
              "code": "INVALID_NOTIFICATION_POLICY"
            }
            """;
}

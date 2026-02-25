package com.polywave.userservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserApiExamples {

  public static final String EXAMPLE_NICKNAME_AVAILABILITY_OK = """
      {
        "available": true
      }
      """;

  public static final String EXAMPLE_RANDOM_NICKNAME_OK = """
      {
        "nickname": "파란당근도사"
      }
      """;

  public static final String EXAMPLE_ASSIGN_NICKNAME_REQUEST = """
      {
        "nickname": "당근도사"
      }
      """;

  public static final String EXAMPLE_ASSIGN_NICKNAME_OK = """
      {}
      """;

  public static final String EXAMPLE_USER_NOT_FOUND = """
      {
        "code": "USER_NOT_FOUND"
      }
      """;

  public static final String EXAMPLE_FORBIDDEN_NICKNAME = """
      {
        "code": "BAD_REQUEST"
      }
      """;

  public static final String EXAMPLE_DUPLICATE_NICKNAME = """
      {
        "code": "DUPLICATE_NICKNAME"
      }
      """;

  public static final String EXAMPLE_VALIDATION_ERROR = """
      {
        "code": "VALIDATION_ERROR"
      }
      """;

  public static final String EXAMPLE_ADDRESS_SEARCH_OK = """
      {
        "totalCount": 1,
        "currentPage": 1,
        "countPerPage": 10,
        "addresses": [
          {
            "sido": "경기도",
            "sigungu": "과천시",
            "emdName": "중앙동"
          }
        ]
      }
      """;

  public static final String EXAMPLE_UPDATE_PROFILE_REQUEST = """
      {
        "gender": "MAN",
        "birthDate": "19990101",
        "sido": "서울특별시",
        "sigungu": "강남구",
        "emdName": "역삼동"
      }
      """;

  public static final String EXAMPLE_UPDATE_PROFILE_OK = """
      {}
      """;

  public static final String EXAMPLE_UPDATE_ONBOARDING_STATUS_REQUEST = """
      {
        "onboardingStatus": "CATEGORY"
      }
      """;

  public static final String EXAMPLE_UPDATE_ONBOARDING_STATUS_OK = """
      {}
      """;

  public static final String EXAMPLE_UPDATE_ONBOARDING_STATUS_VALIDATION_ERROR = """
      {
        "code": "VALIDATION_ERROR"
      }
      """;
}

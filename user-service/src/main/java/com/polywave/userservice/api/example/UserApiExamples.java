package com.polywave.userservice.api.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserApiExamples {

    public static final String EXAMPLE_NICKNAME_AVAILABILITY_OK = """
            {
              "success": true,
              "message": "닉네임 사용 가능 여부 조회 성공",
              "data": {
                "available": true
              }
            }
            """;

    public static final String EXAMPLE_RANDOM_NICKNAME_OK = """
            {
              "success": true,
              "message": "랜덤 닉네임 생성 성공",
              "data": {
                "nickname": "파란당근도사"
              }
            }
            """;

    public static final String EXAMPLE_ASSIGN_NICKNAME_REQUEST = """
            {
              "nickname": "당근도사"
            }
            """;

    public static final String EXAMPLE_ASSIGN_NICKNAME_OK = """
            {
              "success": true,
              "message": "닉네임 설정 성공",
              "data": null
            }
            """;

    public static final String EXAMPLE_USER_NOT_FOUND = """
            {
              "success": false,
              "message": "사용자를 찾을 수 없습니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_FORBIDDEN_NICKNAME = """
            {
              "success": false,
              "message": "금칙어가 포함된 닉네임입니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_DUPLICATE_NICKNAME = """
            {
              "success": false,
              "message": "이미 사용 중인 닉네임입니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_VALIDATION_ERROR = """
            {
              "success": false,
              "message": "닉네임은 한글/숫자만 사용 가능하며, 공백은 단어 사이에 1개만 허용됩니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_ADDRESS_SEARCH_OK = """
            {
              "success": true,
              "message": "주소 검색 성공",
              "data": {
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
            {
              "success": true,
              "message": "프로필 수정 성공",
              "data": null
            }
            """;
}

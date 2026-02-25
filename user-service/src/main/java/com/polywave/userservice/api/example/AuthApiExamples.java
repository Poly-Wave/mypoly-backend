package com.polywave.userservice.api.example;

public final class AuthApiExamples {

    private AuthApiExamples() {}

  // ===== 요청 예시 =====
  public static final String EXAMPLE_SOCIAL_TOKEN_LOGIN_REQUEST_ACCESS_TOKEN = """
      {
        "tokenType": "ACCESS_TOKEN",
        "token": "kakao_access_token_here"
      }
      """;

  public static final String EXAMPLE_SOCIAL_TOKEN_LOGIN_REQUEST_ID_TOKEN = """
      {
        "tokenType": "ID_TOKEN",
        "token": "eyJhbGciOi...google_or_apple_id_token_here"
      }
      """;

  // ===== 성공 응답 예시 =====
  public static final String EXAMPLE_SOCIAL_TOKEN_LOGIN_OK = """
      {
        "userId": 123,
        "provider": "kakao",
        "providerUserId": "3123123",
        "nickname": "당근도사",
        "profileImageUrl": "https://example.com/profile.png",
        "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      """;

  public static final String EXAMPLE_DEV_AUTH_OK = """
      {
        "userId": 999,
        "provider": "dev",
        "providerUserId": "swagger",
        "nickname": "SwaggerUser",
        "profileImageUrl": null,
        "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      """;

  // ===== 실패 응답 예시 =====
  public static final String EXAMPLE_UNSUPPORTED_SOCIAL_LOGIN = """
      {
        "code": "BAD_REQUEST"
      }
      """;

  public static final String EXAMPLE_INVALID_KAKAO_TOKEN = """
      {
        "code": "UNAUTHORIZED"
      }
      """;

  public static final String EXAMPLE_DEV_AUTH_KEY_NOT_SET = """
      {
        "code": "FORBIDDEN"
      }
      """;

  public static final String EXAMPLE_DEV_AUTH_KEY_MISMATCH = """
      {
        "code": "FORBIDDEN"
      }
      """;
}

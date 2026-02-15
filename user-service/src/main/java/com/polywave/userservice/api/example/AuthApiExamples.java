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
              "success": true,
              "message": "소셜 로그인 성공",
              "data": {
                "userId": 123,
                "provider": "kakao",
                "providerUserId": "3123123",
                "nickname": "당근도사",
                "profileImageUrl": "https://example.com/profile.png",
                "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
              }
            }
            """;

    public static final String EXAMPLE_DEV_AUTH_OK = """
            {
              "success": true,
              "message": "dev 로그인 성공",
              "data": {
                "userId": 999,
                "provider": "dev",
                "providerUserId": "swagger",
                "nickname": "SwaggerUser",
                "profileImageUrl": null,
                "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
              }
            }
            """;

    // ===== 실패 응답 예시 =====
    public static final String EXAMPLE_UNSUPPORTED_SOCIAL_LOGIN = """
            {
              "success": false,
              "message": "지원하지 않는 소셜 로그인 요청입니다. provider=naver, tokenType=ACCESS_TOKEN",
              "data": null
            }
            """;

    public static final String EXAMPLE_INVALID_KAKAO_TOKEN = """
            {
              "success": false,
              "message": "카카오 토큰이 만료되었거나 유효하지 않습니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_DEV_AUTH_KEY_NOT_SET = """
            {
              "success": false,
              "message": "dev-auth key가 설정되지 않았습니다.",
              "data": null
            }
            """;

    public static final String EXAMPLE_DEV_AUTH_KEY_MISMATCH = """
            {
              "success": false,
              "message": "dev-auth key가 일치하지 않습니다.",
              "data": null
            }
            """;
}

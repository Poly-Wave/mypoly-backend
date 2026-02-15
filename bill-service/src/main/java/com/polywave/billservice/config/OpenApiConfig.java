package com.polywave.billservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(@Value("${spring.application.name:bill-service}") String appName) {
        String guide = """
                MyPoly %s OpenAPI 문서입니다.

                ## bill-service가 하는 일
                - **요금/정책 카테고리** 목록 제공
                - 로그인 사용자의 **관심 카테고리** 저장(갱신)

                ## 인증(JWT) 안내
                - bill-service는 **JWT를 직접 발급하지 않습니다.**
                - user-service가 발급한 JWT를 그대로 사용합니다.

                ## Swagger에서 인증 테스트 방법(현 구조 기준)

                ### 1) dev/local (앱 없이 가능)
                1) user-service Swagger에서 `POST /dev-auth/login` 호출 (헤더 `X-DEV-KEY`)
                2) 응답 `data.jwt` 복사
                3) bill-service Swagger 우측 상단 **Authorize** → `Bearer {jwt}` 입력
                4) 잠금 아이콘 API(`/categories/interests`) 호출

                ### 2) 실서비스/앱연동
                1) 앱 SDK로 소셜 로그인 → access_token 또는 id_token 획득
                2) user-service `POST /auth/{provider}/token` 으로 JWT 발급
                3) bill-service Swagger에서 Authorize → `Bearer {jwt}` 입력

                ## 참고
                - `GET /categories`는 로그인 없이도 호출 가능합니다.
                """.formatted(appName);

        return new OpenAPI()
                .info(new Info()
                        .title("MyPoly - " + appName + " API")
                        .version("v1")
                        .description(guide))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}

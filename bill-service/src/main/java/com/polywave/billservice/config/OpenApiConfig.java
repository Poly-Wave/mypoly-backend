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
                - **요금/정책 카테고리** 목록을 제공하고,\
                  로그인 사용자의 **관심 카테고리**를 저장합니다.

                ## 인증(JWT) 안내
                - bill-service는 **JWT를 직접 발급하지 않습니다.**\
                  로그인 후 user-service가 발급한 JWT를 그대로 사용합니다.

                ### Swagger에서 인증 테스트 방법
                1) user-service에서 로그인 후 JWT 발급 (예: 브라우저로 `/users/auth/kakao` 접속)
                2) 로그인 성공 응답(JSON)에서 `jwt` 값을 복사
                3) Swagger 우측 상단 **Authorize** 클릭 → 아래 형식으로 입력
                   - `Bearer {jwt}`
                4) 잠금 아이콘이 있는 API 호출로 정상 동작 확인

                ## 참고
                - `GET /categories`는 로그인 없이도 호출 가능합니다.
                - `/auth/{provider}` 같은 리다이렉트 기반 OAuth2 엔드포인트는 Swagger Execute에서\
                  흐름이 깔끔히 보이지 않을 수 있습니다.
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

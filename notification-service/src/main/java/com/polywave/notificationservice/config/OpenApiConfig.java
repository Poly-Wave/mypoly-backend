package com.polywave.notificationservice.config;

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
    public OpenAPI openAPI(@Value("${spring.application.name:notification-service}") String appName) {
        String guide = """
                MyPoly %s OpenAPI 문서입니다.

                ## Swagger만으로 알림 기능 테스트하는 방법(추천)

                ### 1) user-service 에서 JWT 발급
                1) user-service Swagger 에서 `POST /dev-auth/login` 호출 (헤더 X-DEV-KEY)
                2) 응답의 jwt 복사
                3) 본 페이지 우측 상단 Authorize → bearerAuth 에 `Bearer {jwt}` 입력

                ### 2) 알림 정책 관리 (어드민)
                - Authorize → adminApiKey 에 `X-Admin-Api-Key` 값 입력 (local 기본: local-admin-key)
                - `/internal/notification-policies` 로 정책 CRUD

                ### 3) DEV 트리거
                - `/dev-notifications/deliver`: 임의 사용자에게 알림 강제 발급
                - `/dev-notifications/run-onboarding-remind`: 온보딩 리마인더 스케줄러 수동 실행
                """.formatted(appName);

        return new OpenAPI()
                .info(new Info()
                        .title("MyPoly - " + appName + " API")
                        .version("v1")
                        .description(guide))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        .addSecuritySchemes(
                                "adminApiKey",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Admin-Api-Key")
                                        .description("알림 정책 관리(/internal/notification-policies/**) 어드민 API 키")
                        ));
    }
}

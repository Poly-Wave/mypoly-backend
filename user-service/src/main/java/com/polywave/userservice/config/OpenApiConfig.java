package com.polywave.userservice.config;

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
    public OpenAPI openAPI(@Value("${spring.application.name:user-service}") String appName) {
        String guide = """
                MyPoly %s OpenAPI 문서입니다.

                ## Swagger만으로 “전체 기능” 테스트하는 방법(추천)

                ### 1) dev/local: DevAuth로 JWT 발급 (앱 필요 없음)
                1) `POST /dev-auth/login` 호출 (헤더 `X-DEV-KEY` 필요)
                2) 응답의 `data.jwt` 복사
                3) Swagger 우측 상단 **Authorize** → `Bearer {jwt}` 입력
                4) 잠금 아이콘 API(User/UserTerms 등) 호출

                - local 기본값
                  - `social.dev-auth.enabled=true`
                  - `X-DEV-KEY=local-dev-key` (DEV_AUTH_KEY 미주입 시 기본)

                ### 2) 실서비스/앱연동: SDK 토큰 검증 기반 JWT 발급
                1) 앱 SDK로 소셜 로그인 → access_token 또는 id_token 획득
                2) `POST /auth/{provider}/token` 호출 → JWT 발급
                3) Authorize에 `Bearer {jwt}` 입력 후 보호 API 호출

                ### 3) (옵션) dev/local 리다이렉트 로그인 시작점
                - `GET /auth/redirect/{provider}` → `/oauth2/authorization/{provider}` 로 302 이동
                - 브라우저에서만 확인용(Swagger Execute는 비추)

                ## 참고
                - 운영(prod)에서는 Swagger가 꺼져있는 구성이 기본입니다.
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
                        ));
    }
}

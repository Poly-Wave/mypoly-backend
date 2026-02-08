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

                ## Swagger에서 테스트하는 방법
                1) **소셜 로그인 시작**
                   - 브라우저 주소창에서 `/users/auth/kakao` 접속 (Swagger Execute는 리다이렉트/CORS로 제한될 수 있음)
                2) 로그인 성공 후 서버가 내려준 JSON에서 `jwt` 값을 복사
                3) Swagger 우측 상단 **Authorize** 버튼에 JWT 입력
                   - 형식: `Bearer {token}` 또는 `{token}` (프로젝트 설정에 맞게)
                4) 잠금 아이콘이 있는 API 호출로 정상 동작 확인

                ## 참고
                - `/auth/{provider}`는 302 리다이렉트 엔드포인트라 Swagger Execute에서 흐름이 깔끔히 보이지 않을 수 있습니다.
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

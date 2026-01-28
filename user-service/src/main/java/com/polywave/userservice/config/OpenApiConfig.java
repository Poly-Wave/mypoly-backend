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
        return new OpenAPI()
                .info(new Info()
                        .title("MyPoly - " + appName + " API")
                        .version("v1")
                        .description("MyPoly user-service OpenAPI 문서입니다."))
                // JWT 토큰 넣을 Authorize 버튼 준비(추후 보호 API에 붙이기 쉬움)
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

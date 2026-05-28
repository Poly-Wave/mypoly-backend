package com.polywave.billservice.api.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * 날짜 필터 쿼리 파라미터용 OpenAPI 메타데이터.
 * generator가 DateTime으로 생성하지 않도록 schema type을 string으로 고정합니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(schema = @Schema(type = "string"))
public @interface DateQueryParameter {

    String DATE_QUERY_DESCRIPTION_SUFFIX =
            "KST 기준. 2026-05-13 또는 2026-05-13T00:00:00.000Z 형식";

    @AliasFor(annotation = Parameter.class, attribute = "description")
    String description();

    @AliasFor(annotation = Parameter.class, attribute = "example")
    String example() default "2026-05-13";
}

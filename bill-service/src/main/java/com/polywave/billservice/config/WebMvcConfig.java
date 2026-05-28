package com.polywave.billservice.config;

import com.polywave.billservice.config.converter.FlexibleLocalDateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FlexibleLocalDateConverter flexibleLocalDateConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(flexibleLocalDateConverter);
    }
}

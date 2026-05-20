package com.polywave.notificationservice.config;

import com.polywave.common.security.handler.RestAccessDeniedHandler;
import com.polywave.common.security.handler.RestAuthenticationEntryPoint;
import com.polywave.notificationservice.security.AdminApiKeyFilter;
import com.polywave.notificationservice.security.SecurityEndpoints;
import com.polywave.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminApiKeyFilter adminApiKeyFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Value("${notification.dev-trigger.enabled:false}")
    private boolean devTriggerEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(SecurityEndpoints.PUBLIC_ENDPOINTS).permitAll();

                    if (devTriggerEnabled) {
                        auth.requestMatchers(SecurityEndpoints.DEV_ONLY_ENDPOINTS).permitAll();
                    }

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(adminApiKeyFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}

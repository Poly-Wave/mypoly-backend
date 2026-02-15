package com.polywave.userservice.config;

import com.polywave.security.JwtAuthenticationFilter;
import com.polywave.userservice.security.OAuth2LoginFailureHandler;
import com.polywave.userservice.security.OAuth2LoginSuccessHandler;
import com.polywave.userservice.security.SecurityEndpoints;
import com.polywave.userservice.security.handler.RestAccessDeniedHandler;
import com.polywave.userservice.security.handler.RestAuthenticationEntryPoint;
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
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Value("${social.oauth2-login.enabled:false}")
    private boolean oauth2LoginEnabled;

    @Value("${social.dev-auth.enabled:false}")
    private boolean devAuthEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(SecurityEndpoints.PUBLIC_ENDPOINTS).permitAll();

                    if (oauth2LoginEnabled) {
                        auth.requestMatchers(SecurityEndpoints.OAUTH2_ENDPOINTS).permitAll();
                    }
                    if (devAuthEnabled) {
                        auth.requestMatchers(SecurityEndpoints.DEV_ONLY_ENDPOINTS).permitAll();
                    }

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // prod는 stateless 권장, oauth2 리다이렉트 켜는 local/dev는 세션 필요할 수 있음
        if (!oauth2LoginEnabled) {
            http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        }

        // dev/local에서만 oauth2 리다이렉트 플로우를 켜고 싶으면 property로 활성화
        if (oauth2LoginEnabled) {
            http.oauth2Login(oauth2 -> oauth2
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler)
            );
        }

        return http.build();
    }
}

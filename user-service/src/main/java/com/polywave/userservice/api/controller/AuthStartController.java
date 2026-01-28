package com.polywave.userservice.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthStartController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * 공식 로그인 시작 URL
     * - /users/auth/kakao
     * - /users/auth/google
     * - /users/auth/apple
     *
     * 실제 OAuth2 시작 엔드포인트(/oauth2/authorization/{provider})는 Spring Security가 처리.
     */
    @GetMapping("/{provider}")
    public ResponseEntity<Void> start(@PathVariable String provider, HttpServletRequest request) {
        // 설정된 provider만 허용 (application.properties에 registration이 있어야 통과)
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(provider);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }

        // context-path가 /users면 Location은 반드시 /users/oauth2/... 로 내려줘야 함
        String location = request.getContextPath() + "/oauth2/authorization/" + provider;

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .header(HttpHeaders.LOCATION, location)
                .build();
    }
}

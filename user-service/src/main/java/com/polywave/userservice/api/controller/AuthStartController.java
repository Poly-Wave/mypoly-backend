package com.polywave.userservice.api.controller;

import com.polywave.userservice.api.spec.AuthStartApi;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/redirect")
@ConditionalOnProperty(name = "social.oauth2-login.enabled", havingValue = "true")
public class AuthStartController implements AuthStartApi {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public AuthStartController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public ResponseEntity<Void> start(String provider, HttpServletRequest request) {
        ClientRegistration reg = clientRegistrationRepository.findByRegistrationId(provider);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }

        String location = request.getContextPath() + "/oauth2/authorization/" + provider;

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, location)
                .build();
    }
}

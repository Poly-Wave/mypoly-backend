package com.polywave.userservice.application.auth.command;

import com.polywave.userservice.api.dto.SocialTokenType;

/**
 * API 계층(Request DTO)와 애플리케이션 계층(Service) 사이 의존을 줄이기 위한 Command 객체.
 */
public record SocialTokenLoginCommand(
        String provider,
        SocialTokenType tokenType,
        String token
) {
}

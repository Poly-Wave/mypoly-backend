package com.polywave.userservice.security.token;

import com.polywave.userservice.api.dto.SocialTokenType;

public interface SocialTokenVerifier {
    String provider();
    boolean supports(SocialTokenType tokenType);

    SocialVerifiedUser verify(String token, SocialTokenType tokenType);
}

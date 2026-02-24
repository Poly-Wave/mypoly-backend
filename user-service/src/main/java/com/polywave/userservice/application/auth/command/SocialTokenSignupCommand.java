package com.polywave.userservice.application.auth.command;

import com.polywave.userservice.api.dto.SocialTokenType;
import com.polywave.userservice.application.userterms.command.TermsAgreement;

import java.util.List;

public record SocialTokenSignupCommand(
        String provider,
        SocialTokenType tokenType,
        String token,
        String nickname,
        List<TermsAgreement> termsAgreements
) {
}

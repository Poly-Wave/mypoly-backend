package com.polywave.userservice.application.userterms.command;

import java.util.List;

public record UserAgreementCommand(
        Long userId,
        List<TermsAgreement> termsAgreements
) {
}

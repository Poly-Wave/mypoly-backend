package com.polywave.userservice.application.userTerms.command;

import java.util.List;

public record UserAgreementCommand(
        Long userId,
        List<TermsAgreement> termsAgreements
) {
}
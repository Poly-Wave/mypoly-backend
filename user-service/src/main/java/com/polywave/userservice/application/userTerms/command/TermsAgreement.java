package com.polywave.userservice.application.userTerms.command;

public record TermsAgreement(
        Long termId,
        boolean agreed
) {
}

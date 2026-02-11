package com.polywave.userservice.application.userterms.command;

public record TermsAgreement(
        Long termId,
        boolean agreed
) {
}

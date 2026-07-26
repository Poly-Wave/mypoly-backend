package com.polywave.billservice.application.bill.query.result;

public record CoProposerResult(
        Long memberId,
        String name,
        String partyName,
        String photoUrl
) {
}

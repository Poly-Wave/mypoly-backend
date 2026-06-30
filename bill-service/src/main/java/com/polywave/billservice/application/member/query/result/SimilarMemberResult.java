package com.polywave.billservice.application.member.query.result;

public record SimilarMemberResult(
        Long memberId,
        String name,
        String partyName,
        String districtName,
        String photoUrl,
        long comparedVoteCount,
        long matchedVoteCount
) {
}

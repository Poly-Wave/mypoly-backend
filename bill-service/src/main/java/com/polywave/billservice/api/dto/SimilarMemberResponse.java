package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.member.query.result.SimilarMemberResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "나와 투표 성향이 유사한 국회의원")
public record SimilarMemberResponse(
        @Schema(description = "국회의원 ID", example = "1")
        Long memberId,

        @Schema(description = "이름", example = "이수진")
        String name,

        @Schema(description = "정당명", example = "더불어민주당")
        String partyName,

        @Schema(description = "지역구명", example = "서울 동작구")
        String districtName,

        @Schema(description = "의원 사진 URL")
        String photoUrl,

        @Schema(description = "함께 표결한(비교 대상) 의안 수", example = "12")
        long comparedVoteCount,

        @Schema(description = "입장이 일치한 의안 수", example = "9")
        long matchedVoteCount,

        @Schema(description = "유사도. 입장 일치 비율(0.0~1.0), 소수점 3자리", example = "0.75")
        double matchRate
) {

    public static SimilarMemberResponse from(SimilarMemberResult result) {
        return new SimilarMemberResponse(
                result.memberId(),
                nullToEmpty(result.name()),
                nullToEmpty(result.partyName()),
                nullToEmpty(result.districtName()),
                nullToEmpty(result.photoUrl()),
                result.comparedVoteCount(),
                result.matchedVoteCount(),
                calculateMatchRate(result.matchedVoteCount(), result.comparedVoteCount())
        );
    }

    private static double calculateMatchRate(long matched, long compared) {
        if (compared <= 0) {
            return 0.0;
        }
        double rate = (double) matched / compared;
        return Math.round(rate * 1000) / 1000.0;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

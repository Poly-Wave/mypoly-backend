package com.polywave.billservice.api.dto;

import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "국회의원 상세 응답")
public record BillMemberDetailResponse(
        @Schema(description = "국회의원 ID", example = "1")
        Long memberId,

        @Schema(description = "외부 의원 식별자", example = "M123456")
        String externalMemberId,

        @Schema(description = "국회 MONA 코드", example = "ABC123")
        String monaCd,

        @Schema(description = "국회의원 번호", example = "12345")
        String memberNo,

        @Schema(description = "이름", example = "이수진")
        String name,

        @Schema(description = "한자 이름", example = "李秀眞")
        String nameChinese,

        @Schema(description = "영문 이름", example = "LEE SUJIN")
        String nameEnglish,

        @Schema(description = "정당명", example = "더불어민주당")
        String partyName,

        @Schema(description = "지역구명", example = "서울 동작구")
        String districtName,

        @Schema(description = "지역구 유형", example = "지역구")
        String districtType,

        @Schema(description = "소속 위원회", example = "법제사법위원회")
        String committeeName,

        @Schema(description = "현재 소속 위원회", example = "법제사법위원회")
        String currentCommitteeName,

        @Schema(description = "국회 대수", example = "22")
        String era,

        @Schema(description = "당선 유형", example = "지역구")
        String electionType,

        @Schema(description = "성별", example = "여")
        String gender,

        @Schema(description = "생년월일", example = "1970-01-01")
        LocalDate birthDate,

        @Schema(description = "의원 사진 URL")
        String photoUrl,

        @Schema(description = "홈페이지 URL")
        String homepageUrl,

        @Schema(description = "약력")
        String briefHistory
) {

    public static BillMemberDetailResponse from(BillMemberDetailResult result) {
        return new BillMemberDetailResponse(
                result.memberId(),
                nullToEmpty(result.externalMemberId()),
                nullToEmpty(result.monaCd()),
                nullToEmpty(result.memberNo()),
                nullToEmpty(result.name()),
                nullToEmpty(result.nameChinese()),
                nullToEmpty(result.nameEnglish()),
                nullToEmpty(result.partyName()),
                nullToEmpty(result.districtName()),
                nullToEmpty(result.districtType()),
                nullToEmpty(result.committeeName()),
                nullToEmpty(result.currentCommitteeName()),
                nullToEmpty(result.era()),
                nullToEmpty(result.electionType()),
                nullToEmpty(result.gender()),
                result.birthDate(),
                nullToEmpty(result.photoUrl()),
                nullToEmpty(result.homepageUrl()),
                nullToEmpty(result.briefHistory())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
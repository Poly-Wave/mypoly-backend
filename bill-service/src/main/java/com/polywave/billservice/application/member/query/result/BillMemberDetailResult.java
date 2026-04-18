package com.polywave.billservice.application.member.query.result;

import java.time.LocalDate;

public record BillMemberDetailResult(
        Long memberId,
        String externalMemberId,
        String monaCd,
        String memberNo,
        String name,
        String nameChinese,
        String nameEnglish,
        String partyName,
        String districtName,
        String districtType,
        String committeeName,
        String currentCommitteeName,
        String era,
        String electionType,
        String gender,
        LocalDate birthDate,
        String photoUrl,
        String homepageUrl,
        String briefHistory,
        String phoneNumber,
        String officeRoomNumber,
        String email,
        String aideNames,
        String chiefSecretaryNames,
        String secretaryNames
) {
}

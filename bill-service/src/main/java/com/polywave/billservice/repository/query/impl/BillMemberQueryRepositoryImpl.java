package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import com.polywave.billservice.domain.QBillMember;
import com.polywave.billservice.repository.query.BillMemberQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BillMemberQueryRepositoryImpl implements BillMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BillMemberDetailResult> findMemberDetailById(Long memberId) {
        QBillMember member = QBillMember.billMember;

        BillMemberDetailResult result = queryFactory
                .select(Projections.constructor(
                        BillMemberDetailResult.class,
                        member.id,
                        member.externalMemberId,
                        member.monaCd,
                        member.memberNo,
                        member.name,
                        member.nameChinese,
                        member.nameEnglish,
                        member.partyName,
                        member.districtName,
                        member.districtType,
                        member.committeeName,
                        member.currentCommitteeName,
                        member.era,
                        member.electionType,
                        member.gender,
                        member.birthDate,
                        member.photoUrl,
                        member.homepageUrl,
                        member.briefHistory
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
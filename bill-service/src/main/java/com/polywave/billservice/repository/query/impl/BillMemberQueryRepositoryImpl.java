package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import com.polywave.billservice.application.member.query.result.BillMemberRepresentativeBillResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QBillCategory;
import com.polywave.billservice.domain.QBillMember;
import com.polywave.billservice.domain.QBillProposer;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.BillMemberQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
                        member.briefHistory,
                        member.phoneNumber,
                        member.officeRoomNumber,
                        member.email,
                        member.aideNames,
                        member.chiefSecretaryNames,
                        member.secretaryNames
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<BillMemberRepresentativeBillResult> findRepresentativeBillsByMemberId(Long memberId, int limit) {
        QBillProposer proposer = QBillProposer.billProposer;
        QBill bill = QBill.bill;
        QBillAiAnalysis analysis = QBillAiAnalysis.billAiAnalysis;
        QBillAiCategory primaryBillAiCategory = new QBillAiCategory("primaryBillAiCategory");
        QBillCategory primaryCategory = new QBillCategory("primaryCategory");
        QUserBillVote vote = QUserBillVote.userBillVote;

        return queryFactory
                .select(Projections.constructor(
                        BillMemberRepresentativeBillResult.class,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        bill.currentProcStageOrder,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        bill.viewCount.coalesce(0L),
                        vote.id.count()
                ))
                .from(proposer)
                .innerJoin(proposer.bill, bill)
                .leftJoin(analysis).on(
                        analysis.bill.id.eq(bill.id),
                        analysis.current.isTrue()
                )
                .leftJoin(primaryBillAiCategory).on(
                        primaryBillAiCategory.analysis.id.eq(analysis.id),
                        primaryBillAiCategory.rankOrder.eq(1)
                )
                .leftJoin(primaryBillAiCategory.category, primaryCategory)
                .leftJoin(vote).on(vote.bill.id.eq(bill.id))
                .where(
                        proposer.member.id.eq(memberId),
                        proposer.representative.isTrue()
                )
                .groupBy(
                        proposer.id,
                        bill.id,
                        bill.officialTitle,
                        bill.proposalDate,
                        bill.currentProcStageOrder,
                        primaryCategory.code,
                        primaryCategory.name,
                        primaryCategory.backgroundColor,
                        bill.viewCount
                )
                .orderBy(bill.proposalDate.desc().nullsLast(), bill.id.desc())
                .limit(limit)
                .fetch();
    }
}

package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.member.query.result.SimilarMemberResult;
import com.polywave.billservice.domain.QBillMember;
import com.polywave.billservice.domain.QBillVote;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.SimilarMemberQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SimilarMemberQueryRepositoryImpl implements SimilarMemberQueryRepository {

    // 사용자 투표 결과 도메인(UserVoteResult) — 영문 enum 이름.
    private static final String USER_AGREE = "AGREE";
    private static final String USER_DISAGREE = "DISAGREE";

    // 국회의원 표결 결과 도메인(bill_votes.vote_result) — 국회 OpenAPI 한글 원문.
    // 입장 비교는 찬성/반대만 사용하고 기권/불참은 제외한다.
    private static final String MEMBER_AGREE = "찬성";
    private static final String MEMBER_DISAGREE = "반대";

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SimilarMemberResult> findSimilarMembersByUserVotes(Long userId, int minComparedVotes, int limit) {
        QUserBillVote userVote = QUserBillVote.userBillVote;
        QBillVote memberVote = QBillVote.billVote;
        QBillMember member = QBillMember.billMember;

        BooleanExpression agreeMatch =
                userVote.voteResult.eq(USER_AGREE).and(memberVote.voteResult.eq(MEMBER_AGREE));
        BooleanExpression disagreeMatch =
                userVote.voteResult.eq(USER_DISAGREE).and(memberVote.voteResult.eq(MEMBER_DISAGREE));

        NumberExpression<Long> comparedCount = memberVote.id.count();
        NumberExpression<Long> matchedCount = new CaseBuilder()
                .when(agreeMatch.or(disagreeMatch)).then(1L)
                .otherwise(0L)
                .sum();
        NumberExpression<Double> matchRatio = matchedCount.doubleValue().divide(comparedCount);

        return queryFactory
                .select(Projections.constructor(
                        SimilarMemberResult.class,
                        member.id,
                        member.name,
                        member.partyName,
                        member.districtName,
                        member.photoUrl,
                        comparedCount,
                        matchedCount
                ))
                .from(userVote)
                // 같은 의안에 대한 의원 표결만 조인. innerJoin 으로 member_id 가 매핑된 표결만 남는다.
                .innerJoin(memberVote).on(memberVote.bill.id.eq(userVote.bill.id))
                .innerJoin(memberVote.billMember, member)
                .where(
                        userVote.userId.eq(userId),
                        userVote.voteResult.in(USER_AGREE, USER_DISAGREE),
                        memberVote.voteResult.in(MEMBER_AGREE, MEMBER_DISAGREE)
                )
                .groupBy(member.id, member.name, member.partyName, member.districtName, member.photoUrl)
                .having(comparedCount.goe((long) minComparedVotes))
                .orderBy(matchRatio.desc(), comparedCount.desc(), member.id.asc())
                .limit(limit)
                .fetch();
    }
}

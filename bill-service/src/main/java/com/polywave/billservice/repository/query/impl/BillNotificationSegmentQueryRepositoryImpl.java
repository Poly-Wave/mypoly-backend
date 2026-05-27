package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.application.notification.query.result.BillStageChangeResult;
import com.polywave.billservice.application.notification.query.result.BookmarkedUnvotedBillResult;
import com.polywave.billservice.application.notification.query.result.UserInterestAgendaCountResult;
import com.polywave.billservice.domain.QBill;
import com.polywave.billservice.domain.QBillAiAnalysis;
import com.polywave.billservice.domain.QBillAiCategory;
import com.polywave.billservice.domain.QUserBillBookmark;
import com.polywave.billservice.domain.QUserBillInterest;
import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.BillNotificationSegmentQueryRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BillNotificationSegmentQueryRepositoryImpl implements BillNotificationSegmentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BookmarkedUnvotedBillResult> findBookmarkedUnvotedBefore(Instant cutoff) {
        QUserBillBookmark bm = QUserBillBookmark.userBillBookmark;
        QBill b = QBill.bill;
        QUserBillVote v = QUserBillVote.userBillVote;

        // 북마크 했지만 같은 (userId, billId) 로 투표가 없는 행을 NOT EXISTS 로 거른다.
        List<Tuple> rows = jpaQueryFactory
                .select(bm.userId, b.id, b.officialTitle)
                .from(bm)
                .join(bm.bill, b)
                .where(
                        bm.createdAt.lt(cutoff),
                        JPAExpressions.selectOne()
                                .from(v)
                                .where(v.userId.eq(bm.userId).and(v.bill.id.eq(b.id)))
                                .notExists()
                )
                .fetch();

        return rows.stream()
                .map(t -> new BookmarkedUnvotedBillResult(
                        t.get(bm.userId),
                        t.get(b.id),
                        nullToEmpty(t.get(b.officialTitle))
                ))
                .toList();
    }

    @Override
    public List<UserInterestAgendaCountResult> findInterestMatchedAgendaCountsBetween(Instant start, Instant end) {
        QUserBillInterest ubi = QUserBillInterest.userBillInterest;
        QBillAiCategory bac = QBillAiCategory.billAiCategory;
        QBillAiAnalysis baa = QBillAiAnalysis.billAiAnalysis;
        QBill b = QBill.bill;

        // 사용자의 관심 카테고리(ubi.category) ↔ 안건의 AI 카테고리(bac.category) 가 같고,
        // 해당 분석이 현재 활성(current=true) 인 안건 중,
        // first_collected_at 이 [start, end) 인 안건들의 DISTINCT 개수를 사용자별로 집계.
        NumberExpression<Long> distinctBillCount = b.id.countDistinct();

        List<Tuple> rows = jpaQueryFactory
                .select(ubi.userId, distinctBillCount)
                .from(ubi)
                .join(bac).on(bac.category.id.eq(ubi.category.id))
                .join(baa).on(baa.id.eq(bac.analysis.id).and(baa.current.isTrue()))
                .join(b).on(b.id.eq(baa.bill.id))
                .where(
                        b.firstCollectedAt.goe(start),
                        b.firstCollectedAt.lt(end)
                )
                .groupBy(ubi.userId)
                .fetch();

        return rows.stream()
                .map(t -> {
                    Long count = t.get(distinctBillCount);
                    return new UserInterestAgendaCountResult(
                            t.get(ubi.userId),
                            count == null ? 0L : count
                    );
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BillStageChangeResult> findRecentBookmarkedStageChanges(Instant since) {
        // since 이후의 단계 전이 row(curr) 와 같은 의안의 직전 전이 row(prev) 를 LATERAL 로 짝지운다.
        // 북마크는 curr.observed_at 보다 먼저 저장된 것만 대상 (변경 후 북마크한 사람은 알릴 의미 없음).
        String sql = """
                SELECT
                  bm.user_id        AS user_id,
                  b.id              AS bill_id,
                  b.official_title  AS bill_title,
                  prev.proc_stage_code AS from_code,
                  prev.proc_stage_name AS from_name,
                  curr.proc_stage_code AS to_code,
                  curr.proc_stage_name AS to_name
                FROM bill_service.bill_status_history curr
                JOIN bill_service.bills b
                  ON b.id = curr.bill_id
                JOIN bill_service.user_bill_bookmarks bm
                  ON bm.bill_id = b.id
                 AND bm.created_at < curr.observed_at
                LEFT JOIN LATERAL (
                  SELECT proc_stage_code, proc_stage_name
                  FROM bill_service.bill_status_history h
                  WHERE h.bill_id = curr.bill_id
                    AND h.observed_at < curr.observed_at
                  ORDER BY h.observed_at DESC
                  LIMIT 1
                ) prev ON true
                WHERE curr.observed_at >= :since
                """;

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("since", Timestamp.from(since))
                .getResultList();

        return rows.stream()
                .map(r -> new BillStageChangeResult(
                        toLong(r[0]),
                        toLong(r[1]),
                        nullToEmpty((String) r[2]),
                        (String) r[3],
                        (String) r[4],
                        nullToEmpty((String) r[5]),
                        nullToEmpty((String) r[6])
                ))
                .toList();
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        if (v instanceof BigInteger bi) return bi.longValueExact();
        return Long.valueOf(v.toString());
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

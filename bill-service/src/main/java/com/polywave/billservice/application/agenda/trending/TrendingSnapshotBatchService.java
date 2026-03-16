package com.polywave.billservice.application.agenda.trending;

import com.polywave.billservice.domain.BillTrendingSnapshot;
import com.polywave.billservice.domain.QAssemblyBillVote;
import com.polywave.billservice.domain.agenda.TrendingCriteria;
import com.polywave.billservice.repository.command.BillTrendingSnapshotCommandRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 7일 내 투표 완료 수를 집계하여 bill_trending_snapshot에 저장하는 배치.
 * 요즘 핫한 탭 조회 시 이 스냅샷을 사용한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingSnapshotBatchService {

    private final JPAQueryFactory queryFactory;
    private final BillTrendingSnapshotCommandRepository snapshotRepository;

    /**
     * 최근 7일간 의안별 투표 완료 수를 집계한 뒤 스냅샷 테이블을 갱신한다.
     * 매시 0분에 실행 (cron: 0 0 * * * *)
     */
    @Scheduled(cron = "${bill.agenda.trending.snapshot-cron:0 0 * * * *}")
    @Transactional
    public void run() {
        Instant cutoff = Instant.now().minus(TrendingCriteria.DAYS, ChronoUnit.DAYS);

        QAssemblyBillVote vote = QAssemblyBillVote.assemblyBillVote;

        List<TrendingCountRow> rows = queryFactory
                .select(
                        Projections.constructor(
                                TrendingCountRow.class,
                                vote.bill.id,
                                vote.id.count()
                        )
                )
                .from(vote)
                .where(vote.voteDate.isNotNull().and(vote.voteDate.goe(cutoff)))
                .groupBy(vote.bill.id)
                .fetch();

        snapshotRepository.deleteAllInBatch();

        List<BillTrendingSnapshot> snapshots = rows.stream()
                .map(row -> new BillTrendingSnapshot(
                        row.billId(),
                        row.voteCount().intValue(),
                        Instant.now()
                ))
                .toList();

        snapshotRepository.saveAll(snapshots);
        log.info("Trending snapshot updated: {} bills, cutoff={}", snapshots.size(), cutoff);
    }

    public record TrendingCountRow(Long billId, Long voteCount) {
    }
}

package com.polywave.billservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bill_popular_view_ranking")
public class BillPopularViewRanking {

    @EmbeddedId
    private BillPopularViewRankingId id;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "view_count_weekly", nullable = false)
    private Long viewCountWeekly;

    @Column(name = "previous_rank")
    private Short previousRank;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    public BillPopularViewRanking(
            BillPopularViewRankingId id,
            Long billId,
            Long viewCountWeekly,
            Short previousRank,
            Instant calculatedAt) {
        this.id = id;
        this.billId = billId;
        this.viewCountWeekly = viewCountWeekly;
        this.previousRank = previousRank;
        this.calculatedAt = calculatedAt;
    }
}

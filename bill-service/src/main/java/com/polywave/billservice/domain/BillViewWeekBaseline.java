package com.polywave.billservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bill_view_week_baseline")
public class BillViewWeekBaseline {

    @EmbeddedId
    private BillViewWeekBaselineId id;

    @Column(name = "baseline_view_count", nullable = false)
    private Long baselineViewCount;

    public BillViewWeekBaseline(BillViewWeekBaselineId id, Long baselineViewCount) {
        this.id = id;
        this.baselineViewCount = baselineViewCount;
    }
}

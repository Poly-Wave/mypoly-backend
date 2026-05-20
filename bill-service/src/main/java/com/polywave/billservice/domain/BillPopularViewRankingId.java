package com.polywave.billservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BillPopularViewRankingId implements Serializable {

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "rank", nullable = false)
    private Short rank;

    public BillPopularViewRankingId(LocalDate weekStart, Short rank) {
        this.weekStart = weekStart;
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillPopularViewRankingId that)) {
            return false;
        }
        return Objects.equals(weekStart, that.weekStart) && Objects.equals(rank, that.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekStart, rank);
    }
}

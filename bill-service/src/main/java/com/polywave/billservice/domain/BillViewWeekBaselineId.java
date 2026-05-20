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
public class BillViewWeekBaselineId implements Serializable {

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    public BillViewWeekBaselineId(LocalDate weekStart, Long billId) {
        this.weekStart = weekStart;
        this.billId = billId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillViewWeekBaselineId that)) {
            return false;
        }
        return Objects.equals(weekStart, that.weekStart) && Objects.equals(billId, that.billId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekStart, billId);
    }
}

package com.polywave.billservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 7일 내 투표 완료 수 배치 선계산 결과.
 * 요즘 핫한 탭 조회 시 이 테이블 기준으로 정렬.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bill_trending_snapshot")
public class BillTrendingSnapshot {

    @Id
    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "vote_count_7d", nullable = false)
    private Integer voteCount7d;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    public BillTrendingSnapshot(Long billId, Integer voteCount7d, Instant calculatedAt) {
        this.billId = billId;
        this.voteCount7d = voteCount7d;
        this.calculatedAt = calculatedAt;
    }
}

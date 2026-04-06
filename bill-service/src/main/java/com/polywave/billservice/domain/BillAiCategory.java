package com.polywave.billservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "bill_ai_categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_bill_ai_categories_analysis_id_category_id", columnNames = {"analysis_id", "category_id"}),
                @UniqueConstraint(name = "uk_bill_ai_categories_analysis_id_rank_order", columnNames = {"analysis_id", "rank_order"})
        },
        indexes = {
                @Index(name = "idx_bill_ai_categories_analysis_id", columnList = "analysis_id"),
                @Index(name = "idx_bill_ai_categories_category_id", columnList = "category_id")
        }
)
public class BillAiCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private BillAiAnalysis analysis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private BillCategory category;

    @Column(name = "rank_order", nullable = false)
    private Integer rankOrder = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

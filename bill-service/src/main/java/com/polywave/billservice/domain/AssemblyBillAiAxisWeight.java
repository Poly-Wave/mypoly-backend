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
        name = "assembly_bill_ai_axis_weights",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assembly_bill_ai_axis_weights_unique", columnNames = {"analysis_id", "opinion_type", "axis_code"})
        },
        indexes = {
                @Index(name = "idx_assembly_bill_ai_axis_weights_analysis_id", columnList = "analysis_id")
        }
)
public class AssemblyBillAiAxisWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AssemblyBillAiAnalysis analysis;

    @Column(name = "opinion_type", length = 20, nullable = false)
    private String opinionType;  // FOR, AGAINST

    @Column(name = "axis_code", length = 2, nullable = false)
    private String axisCode;  // P, M, U, T, N, S, O, R

    @Column(name = "weight", nullable = false)
    private Integer weight = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

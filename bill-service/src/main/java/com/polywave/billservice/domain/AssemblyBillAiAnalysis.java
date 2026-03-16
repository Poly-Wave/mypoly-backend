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
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "assembly_bill_ai_analyses",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assembly_bill_ai_analyses_bill_version", columnNames = {"bill_id", "analysis_version"})
        },
        indexes = {
                @Index(name = "idx_assembly_bill_ai_analyses_bill_id", columnList = "bill_id"),
                @Index(name = "idx_assembly_bill_ai_analyses_is_current", columnList = "bill_id, is_current"),
                @Index(name = "idx_assembly_bill_ai_analyses_generated_at", columnList = "generated_at")
        }
)
public class AssemblyBillAiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private AssemblyBill bill;

    @Column(name = "analysis_version", nullable = false)
    private Integer analysisVersion;

    @Column(name = "analysis_status", length = 20, nullable = false)
    private String analysisStatus;  // SUCCESS, FAILED, SKIPPED

    @Column(name = "headline", length = 300)
    private String headline;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "source_text_hash", length = 64)
    private String sourceTextHash;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "prompt_version", length = 50)
    private String promptVersion;

    @Column(name = "temperature", precision = 4, scale = 2)
    private BigDecimal temperature;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_input", columnDefinition = "jsonb")
    private String analysisInput;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_output", columnDefinition = "jsonb")
    private String analysisOutput;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "is_current", nullable = false)
    private boolean current = false;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

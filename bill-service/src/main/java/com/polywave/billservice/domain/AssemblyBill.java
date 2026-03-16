package com.polywave.billservice.domain;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "assembly_bills",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assembly_bills_external_bill_id", columnNames = "external_bill_id")
        },
        indexes = {
                @Index(name = "idx_assembly_bills_bill_no", columnList = "bill_no"),
                @Index(name = "idx_assembly_bills_proposal_date", columnList = "proposal_date"),
                @Index(name = "idx_assembly_bills_stage_order", columnList = "current_proc_stage_order"),
                @Index(name = "idx_assembly_bills_pass_gubn", columnList = "current_pass_gubn"),
                @Index(name = "idx_assembly_bills_last_collected_at", columnList = "last_collected_at")
        }
)
public class AssemblyBill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_bill_id", length = 100, nullable = false, unique = true)
    private String externalBillId;

    @Column(name = "bill_no", length = 50)
    private String billNo;

    @Column(name = "official_title", length = 500, nullable = false)
    private String officialTitle;

    @Column(name = "proposal_date")
    private LocalDate proposalDate;

    @Column(name = "proposer_kind", length = 50)
    private String proposerKind;

    @Column(name = "representative_proposer_name", length = 100)
    private String representativeProposerName;

    @Column(name = "proposer_count", nullable = false)
    private Integer proposerCount = 0;

    @Column(name = "summary_raw", columnDefinition = "TEXT")
    private String summaryRaw;

    @Column(name = "summary_raw_hash", length = 64)
    private String summaryRawHash;

    @Column(name = "detail_url", length = 500)
    private String detailUrl;

    @Column(name = "current_proc_stage_code", length = 50)
    private String currentProcStageCode;

    @Column(name = "current_proc_stage_name", length = 100)
    private String currentProcStageName;

    @Column(name = "current_proc_stage_order")
    private Integer currentProcStageOrder;

    @Column(name = "current_pass_gubn", length = 50)
    private String currentPassGubn;

    @Column(name = "current_general_result", length = 500)
    private String currentGeneralResult;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_payload", columnDefinition = "jsonb")
    private String sourcePayload;

    @Column(name = "first_collected_at", nullable = false)
    private Instant firstCollectedAt;

    @Column(name = "last_collected_at", nullable = false)
    private Instant lastCollectedAt;

    @Column(name = "last_status_changed_at")
    private Instant lastStatusChangedAt;
}

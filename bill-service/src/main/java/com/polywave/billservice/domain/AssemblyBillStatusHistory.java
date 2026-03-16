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
        name = "assembly_bill_status_history",
        indexes = {
                @Index(name = "idx_assembly_bill_status_history_bill_id", columnList = "bill_id"),
                @Index(name = "idx_assembly_bill_status_history_proc_date", columnList = "proc_date"),
                @Index(name = "idx_assembly_bill_status_history_observed_at", columnList = "observed_at")
        }
)
public class AssemblyBillStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private AssemblyBill bill;

    @Column(name = "proc_stage_code", length = 50)
    private String procStageCode;

    @Column(name = "proc_stage_name", length = 100)
    private String procStageName;

    @Column(name = "proc_stage_order")
    private Integer procStageOrder;

    @Column(name = "pass_gubn", length = 50)
    private String passGubn;

    @Column(name = "general_result", length = 500)
    private String generalResult;

    @Column(name = "proc_date")
    private LocalDate procDate;

    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_payload", columnDefinition = "jsonb")
    private String statusPayload;
}

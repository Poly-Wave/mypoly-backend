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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "assembly_bill_proposers",
        indexes = {
                @Index(name = "idx_assembly_bill_proposers_bill_id", columnList = "bill_id"),
                @Index(name = "idx_assembly_bill_proposers_name", columnList = "proposer_name")
        }
)
public class AssemblyBillProposer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private AssemblyBill bill;

    @Column(name = "proposer_name", length = 100, nullable = false)
    private String proposerName;

    @Column(name = "proposer_type", length = 50)
    private String proposerType;

    @Column(name = "is_representative", nullable = false)
    private boolean representative = false;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_payload", columnDefinition = "jsonb")
    private String sourcePayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

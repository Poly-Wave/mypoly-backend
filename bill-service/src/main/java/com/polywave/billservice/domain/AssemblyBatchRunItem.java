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
        name = "assembly_batch_run_items",
        indexes = {
                @Index(name = "idx_assembly_batch_run_items_batch_run_id", columnList = "batch_run_id"),
                @Index(name = "idx_assembly_batch_run_items_item_type", columnList = "item_type"),
                @Index(name = "idx_assembly_batch_run_items_target_external_id", columnList = "target_external_id")
        }
)
public class AssemblyBatchRunItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_run_id", nullable = false)
    private AssemblyBatchRun batchRun;

    @Column(name = "item_type", length = 30, nullable = false)
    private String itemType;

    @Column(name = "target_external_id", length = 200)
    private String targetExternalId;

    @Column(name = "target_internal_id")
    private Long targetInternalId;

    @Column(name = "item_status", length = 20, nullable = false)
    private String itemStatus;  // SUCCESS, SKIPPED, FAILED

    @Column(name = "action_type", length = 20)
    private String actionType;  // INSERT, UPDATE, NO_CHANGE, DELETE

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

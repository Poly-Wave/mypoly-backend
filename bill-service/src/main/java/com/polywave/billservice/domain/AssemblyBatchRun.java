package com.polywave.billservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "assembly_batch_runs",
        indexes = {
                @Index(name = "idx_assembly_batch_runs_job_type", columnList = "job_type"),
                @Index(name = "idx_assembly_batch_runs_run_status", columnList = "run_status"),
                @Index(name = "idx_assembly_batch_runs_started_at", columnList = "started_at")
        }
)
public class AssemblyBatchRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", length = 50, nullable = false)
    private String jobType;

    @Column(name = "trigger_type", length = 20, nullable = false)
    private String triggerType;  // CRON, MANUAL, RETRY

    @Column(name = "run_status", length = 20, nullable = false)
    private String runStatus;  // RUNNING, SUCCESS, PARTIAL_SUCCESS, FAILED

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "image_tag", length = 100)
    private String imageTag;

    @Column(name = "git_commit_sha", length = 64)
    private String gitCommitSha;

    @Column(name = "records_found", nullable = false)
    private Integer recordsFound = 0;

    @Column(name = "records_inserted", nullable = false)
    private Integer recordsInserted = 0;

    @Column(name = "records_updated", nullable = false)
    private Integer recordsUpdated = 0;

    @Column(name = "records_skipped", nullable = false)
    private Integer recordsSkipped = 0;

    @Column(name = "records_failed", nullable = false)
    private Integer recordsFailed = 0;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

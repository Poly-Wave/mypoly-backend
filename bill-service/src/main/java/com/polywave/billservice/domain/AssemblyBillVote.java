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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "assembly_bill_votes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assembly_bill_votes_external_vote_key", columnNames = "external_vote_key")
        },
        indexes = {
                @Index(name = "idx_assembly_bill_votes_bill_id", columnList = "bill_id"),
                @Index(name = "idx_assembly_bill_votes_member_id", columnList = "member_id"),
                @Index(name = "idx_assembly_bill_votes_vote_result", columnList = "vote_result"),
                @Index(name = "idx_assembly_bill_votes_vote_date", columnList = "vote_date")
        }
)
public class AssemblyBillVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private AssemblyBill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private AssemblyMember member;

    @Column(name = "external_vote_key", length = 200, nullable = false, unique = true)
    private String externalVoteKey;

    @Column(name = "member_name", length = 100)
    private String memberName;

    @Column(name = "member_no", length = 50)
    private String memberNo;

    @Column(name = "mona_cd", length = 50)
    private String monaCd;

    @Column(name = "party_name_snapshot", length = 100)
    private String partyNameSnapshot;

    @Column(name = "district_name_snapshot", length = 200)
    private String districtNameSnapshot;

    @Column(name = "committee_name_snapshot", length = 200)
    private String committeeNameSnapshot;

    @Column(name = "vote_result", length = 50, nullable = false)
    private String voteResult;

    @Column(name = "vote_date")
    private Instant voteDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_payload", columnDefinition = "jsonb")
    private String sourcePayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

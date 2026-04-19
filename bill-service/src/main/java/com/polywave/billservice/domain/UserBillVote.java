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
        name = "user_bill_votes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_bill_votes_user_bill", columnNames = {"user_id", "bill_id"})
        },
        indexes = {
                @Index(name = "idx_user_bill_votes_user", columnList = "user_id"),
                @Index(name = "idx_user_bill_votes_bill", columnList = "bill_id"),
                @Index(name = "idx_user_bill_votes_voted_at", columnList = "voted_at")
        }
)
public class UserBillVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @Column(name = "vote_result", length = 50, nullable = false)
    private String voteResult;

    @Column(name = "voter_age_band", length = 20)
    private String voterAgeBand;

    @Column(name = "voted_at", nullable = false)
    private Instant votedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static UserBillVote create(Long userId, Bill bill, String voteResult, String voterAgeBand, Instant votedAt) {
        UserBillVote vote = new UserBillVote();
        vote.userId = userId;
        vote.bill = bill;
        vote.voteResult = voteResult;
        vote.voterAgeBand = voterAgeBand;
        vote.votedAt = votedAt;
        return vote;
    }

    public void changeVote(String voteResult, String voterAgeBand, Instant votedAt) {
        this.voteResult = voteResult;
        this.voterAgeBand = voterAgeBand;
        this.votedAt = votedAt;
    }
}

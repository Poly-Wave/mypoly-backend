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
        name = "user_bill_interests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_bill_interest_user_category",
                        columnNames = {"user_id", "category_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_bill_interest_user", columnList = "user_id"),
                @Index(name = "idx_user_bill_interest_category", columnList = "category_id")
        }
)
public class UserBillInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private BillCategory category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static UserBillInterest of(Long userId, BillCategory category) {
        UserBillInterest interest = new UserBillInterest();
        interest.userId = userId;
        interest.category = category;
        return interest;
    }
}
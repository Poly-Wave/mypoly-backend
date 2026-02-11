package com.polywave.userservice.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_terms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_terms_user_terms",
                        columnNames = {"user_id", "terms_id"}
                )
        }
)
@Entity
public class UserTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    @Column(nullable = false)
    private boolean agreed;

    @Column(nullable = false)
    private Instant agreedAt;

    public void updateAgreement(boolean agreed, Instant agreedAt) {
        this.agreed = agreed;
        this.agreedAt = agreedAt;
    }
}

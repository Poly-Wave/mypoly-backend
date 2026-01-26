package com.polywave.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_terms")
@Entity
public class UserTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(nullable = false)
    private LocalDateTime agreedAt;
}

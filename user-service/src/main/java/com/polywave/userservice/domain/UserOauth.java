package com.polywave.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_oauths", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerUserId"})
})
@Entity
public class UserOauth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 10, nullable = false)
    private String provider;

    @Column(length = 100, nullable = false)
    private String providerUserId;
}

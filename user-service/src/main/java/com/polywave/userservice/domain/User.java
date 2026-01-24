package com.polywave.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String nickname;

    @Column(length = 10)
    private String gender;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(length = 10)
    private String sido;

    @Column(length = 10)
    private String sigungu;

    @Column(length = 10)
    private String onboardingStatus;
}

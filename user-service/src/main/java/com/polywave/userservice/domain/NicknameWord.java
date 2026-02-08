package com.polywave.userservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name="nickname_words")
public class NicknameWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NicknameWordType type;

    @Column(nullable = false)
    private boolean active = true;

}

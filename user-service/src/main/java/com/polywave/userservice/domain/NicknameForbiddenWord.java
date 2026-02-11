package com.polywave.userservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nickname_forbidden_words")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NicknameForbiddenWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    /**
     * 주의:
     * - DB에 DEFAULT TRUE가 있어도, JPA는 INSERT 시 active 값을 명시해서 넣는 경우가 많음
     * - 필드 기본값이 false(primitive boolean)이면 의도치 않게 비활성으로 저장될 수 있어
     *   코드 레벨 기본값을 TRUE로 맞춰둠
     */
    @Column(nullable = false)
    private boolean active = true;
}

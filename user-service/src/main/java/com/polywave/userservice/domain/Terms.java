package com.polywave.userservice.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms")
@Entity
public class Terms extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 약관 종류(예: TERMS_OF_SERVICE, PRIVACY_POLICY 등)
    @Column(length = 50, nullable = false)
    private String name;

    // 화면에 노출되는 제목
    @Column(length = 100, nullable = false)
    private String title;

    /**
     * 약관 본문(HTML)
     * - PostgreSQL TEXT 컬럼 사용
     * - @Lob 을 쓰면 PostgreSQL에서 OID(CLOB)로 매핑되어 Flyway(TEXT)와 충돌할 수 있음
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 버전(약관 개정 시 증가)
    @Column(nullable = false)
    private Integer version;

    // 필수/선택 약관
    @Column(nullable = false)
    private Boolean required;

    // 시행일(선택)
    private LocalDate effectiveFrom;

    public boolean isRequired() {
        return required != null && required;
    }
}

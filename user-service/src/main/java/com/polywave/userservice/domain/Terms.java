package com.polywave.userservice.domain;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    /**
     * 시행일(날짜 의미)
     * - '시각'이 아니라 '날짜'가 의미인 값이라 LocalDate가 정배
     * - 서버/DB/클라이언트 타임존이 바뀌어도 날짜가 밀리지 않음
     */
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    public boolean isRequired() {
        return required != null && required;
    }
}

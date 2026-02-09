package com.polywave.userservice.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "terms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_terms_name_version",
                        columnNames = {"name", "version"}
                )
        }
)
@Entity
public class Terms extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 약관 종류 키(코드)
     * 예: "TERMS_OF_SERVICE", "PRIVACY_POLICY"
     */
    @Column(length = 50, nullable = false)
    private String name;

    /**
     * 사용자에게 보여줄 제목
     * 예: "서비스 이용약관", "개인정보 처리방침"
     */
    @Column(length = 100, nullable = false)
    private String title;

    /**
     * 약관 전문(긴 텍스트)
     */
    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * 버전(최신 조회/정렬을 위해 int로 고정)
     * 예: 1,2,3...
     */
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean required;

    /**
     * 시행일(없으면 null 허용)
     * - 필요 없으면 그냥 null로 두면 됨
     */
    private LocalDate effectiveFrom;
}

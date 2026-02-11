package com.polywave.userservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "administrative_area", indexes = {
        @Index(name = "idx_admin_area_sido", columnList = "sido"),
        @Index(name = "idx_admin_area_sigungu", columnList = "sigungu"),
        @Index(name = "idx_admin_area_emd", columnList = "emdName"),
        @Index(name = "idx_admin_area_full", columnList = "sido, sigungu, emdName")
})
public class AdministrativeArea extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String sido;

    @Column(length = 20)
    private String sigungu;

    @Column(nullable = false, length = 20)
    private String emdName;
}

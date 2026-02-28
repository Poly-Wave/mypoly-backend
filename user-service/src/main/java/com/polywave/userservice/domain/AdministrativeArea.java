package com.polywave.userservice.domain;

import com.polywave.common.domain.BaseEntity;
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
        @Index(name = "idx_admin_area_emd", columnList = "emd_name"),
        @Index(name = "idx_admin_area_full", columnList = "sido, sigungu, emd_name")
})
public class AdministrativeArea extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String sido;

    @Column(length = 20)
    private String sigungu;

    @Column(nullable = false, length = 20, name = "emd_name")
    private String emdName;
}

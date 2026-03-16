package com.polywave.billservice.domain;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "assembly_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assembly_members_external_member_id", columnNames = "external_member_id")
        },
        indexes = {
                @Index(name = "idx_assembly_members_name", columnList = "name"),
                @Index(name = "idx_assembly_members_party_name", columnList = "party_name"),
                @Index(name = "idx_assembly_members_mona_cd", columnList = "mona_cd"),
                @Index(name = "idx_assembly_members_member_no", columnList = "member_no")
        }
)
public class AssemblyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_member_id", length = 100, nullable = false, unique = true)
    private String externalMemberId;

    @Column(name = "mona_cd", length = 50)
    private String monaCd;

    @Column(name = "member_no", length = 50)
    private String memberNo;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "name_chinese", length = 100)
    private String nameChinese;

    @Column(name = "name_english", length = 200)
    private String nameEnglish;

    @Column(name = "party_name", length = 100)
    private String partyName;

    @Column(name = "district_name", length = 200)
    private String districtName;

    @Column(name = "district_type", length = 100)
    private String districtType;

    @Column(name = "committee_name", length = 500)
    private String committeeName;

    @Column(name = "current_committee_name", length = 200)
    private String currentCommitteeName;

    @Column(name = "era", length = 100)
    private String era;

    @Column(name = "election_type", length = 50)
    private String electionType;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "homepage_url", length = 500)
    private String homepageUrl;

    @Column(name = "brief_history", columnDefinition = "TEXT")
    private String briefHistory;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_payload", columnDefinition = "jsonb")
    private String sourcePayload;
}

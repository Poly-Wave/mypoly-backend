package com.polywave.billservice.domain;

import com.polywave.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "user_bill_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_bill_bookmarks_user_bill",
                        columnNames = {"user_id", "bill_id"}
                )
        }
)
public class UserBillBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    public static UserBillBookmark create(Long userId, Bill bill) {
        UserBillBookmark bookmark = new UserBillBookmark();
        bookmark.userId = userId;
        bookmark.bill = bill;
        return bookmark;
    }
}
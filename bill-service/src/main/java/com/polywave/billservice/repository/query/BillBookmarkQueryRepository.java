package com.polywave.billservice.repository.query;

import com.polywave.billservice.api.dto.BillBookmarkSortType;
import com.polywave.billservice.application.bookmark.query.result.BookmarkedBillResult;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface BillBookmarkQueryRepository {

    boolean existsBookmark(Long userId, Long billId);

    List<BookmarkedBillResult> findBookmarkedBills(
            Long userId,
            Instant fromBookmarkedAt,
            Instant toBookmarkedAtExclusive,
            Set<String> categoryCodes,
            Set<String> stageCodes,
            BillBookmarkSortType sortType,
            Pageable pageable
    );
}
package com.polywave.billservice.application.bookmark.query.service;

import com.polywave.billservice.api.dto.BillBookmarkSortType;
import com.polywave.billservice.api.dto.BookmarkedBillResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.application.bill.query.service.BillUiStage;
import com.polywave.billservice.application.bookmark.query.result.BookmarkedBillResult;
import com.polywave.billservice.repository.query.BillBookmarkQueryRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillBookmarkQueryService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final BillBookmarkQueryRepository billBookmarkQueryRepository;

    public SliceResponse<BookmarkedBillResponse> getBookmarkedBills(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate,
            Set<String> categoryCodes,
            Set<String> stageCodes,
            BillBookmarkSortType sortType,
            Pageable pageable
    ) {
        Instant fromBookmarkedAt = toStartOfDayInstant(fromDate);
        Instant toBookmarkedAtExclusive = toExclusiveEndInstant(toDate);

        Set<String> normalizedCategoryCodes = normalizeCodes(categoryCodes);
        Set<String> normalizedStageCodes = normalizeStageCodes(stageCodes);

        List<BookmarkedBillResult> results = billBookmarkQueryRepository.findBookmarkedBills(
                userId,
                fromBookmarkedAt,
                toBookmarkedAtExclusive,
                normalizedCategoryCodes,
                normalizedStageCodes,
                sortType == null ? BillBookmarkSortType.LATEST : sortType,
                pageable
        );

        int pageSize = pageable.getPageSize();
        boolean hasNext = results.size() > pageSize;
        List<BookmarkedBillResponse> content = results.stream()
                .limit(pageSize)
                .map(BookmarkedBillResponse::from)
                .toList();

        return SliceResponse.of(
                content,
                pageable.getPageNumber(),
                pageSize,
                hasNext
        );
    }

    public boolean isBookmarked(Long userId, Long billId) {
        return billBookmarkQueryRepository.existsBookmark(userId, billId);
    }

    private Instant toStartOfDayInstant(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(KST).toInstant();
    }

    private Instant toExclusiveEndInstant(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.plusDays(1).atStartOfDay(KST).toInstant();
    }

    private Set<String> normalizeCodes(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Set.of();
        }

        return codes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(code -> code.trim().toUpperCase())
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> normalizeStageCodes(Set<String> stageCodes) {
        if (stageCodes == null || stageCodes.isEmpty()) {
            return Set.of();
        }

        return stageCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(code -> code.trim().toUpperCase())
                .filter(code -> BillUiStage.findByCode(code).isPresent())
                .collect(Collectors.toUnmodifiableSet());
    }
}
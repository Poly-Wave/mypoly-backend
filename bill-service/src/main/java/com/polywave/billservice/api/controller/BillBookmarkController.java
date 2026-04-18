package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.BillBookmarkSortType;
import com.polywave.billservice.api.dto.BookmarkedBillResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.api.spec.BillBookmarkApi;
import com.polywave.billservice.application.bookmark.query.service.BillBookmarkQueryService;
import com.polywave.security.annotation.LoginUser;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillBookmarkController implements BillBookmarkApi {

    private final BillBookmarkQueryService billBookmarkQueryService;

    @Override
    public ResponseEntity<SliceResponse<BookmarkedBillResponse>> getBookmarkedBills(
            LocalDate fromDate,
            LocalDate toDate,
            Set<String> categoryCodes,
            Set<String> stageCodes,
            BillBookmarkSortType sortType,
            @LoginUser Long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                billBookmarkQueryService.getBookmarkedBills(
                        userId,
                        fromDate,
                        toDate,
                        categoryCodes,
                        stageCodes,
                        sortType,
                        pageable
                )
        );
    }
}
package com.polywave.billservice.application.bookmark;

import com.polywave.billservice.api.dto.BillBookmarkStatusResponse;
import com.polywave.billservice.application.bookmark.command.service.BillBookmarkCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillBookmarkAppService {

    private final BillBookmarkCommandService billBookmarkCommandService;

    public BillBookmarkStatusResponse bookmark(Long userId, Long billId) {
        billBookmarkCommandService.bookmark(userId, billId);
        return BillBookmarkStatusResponse.of(billId, true);
    }

    public BillBookmarkStatusResponse unbookmark(Long userId, Long billId) {
        billBookmarkCommandService.unbookmark(userId, billId);
        return BillBookmarkStatusResponse.of(billId, false);
    }
}
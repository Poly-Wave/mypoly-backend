package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.BillBookmarkStatusResponse;
import com.polywave.billservice.api.dto.BillDetailResponse;
import com.polywave.billservice.api.dto.BillStatusHistoryResponse;
import com.polywave.billservice.api.dto.BillVoteSummaryResponse;
import com.polywave.billservice.api.dto.SimilarTopicBillResponse;
import com.polywave.billservice.api.dto.SimilarTopicSortType;
import com.polywave.billservice.api.spec.BillDetailApi;
import com.polywave.billservice.application.bill.BillDetailAppService;
import com.polywave.billservice.application.bill.query.service.BillDetailQueryService;
import com.polywave.billservice.application.bookmark.BillBookmarkAppService;
import com.polywave.security.annotation.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillDetailController implements BillDetailApi {

    private final BillDetailAppService billDetailAppService;
    private final BillDetailQueryService billDetailQueryService;
    private final BillBookmarkAppService billBookmarkAppService;

    @Override
    public ResponseEntity<BillDetailResponse> getBillDetail(Long billId, @LoginUser Long userId) {
        return ResponseEntity.ok(billDetailAppService.getBillDetail(billId, userId));
    }

    @Override
    public ResponseEntity<BillBookmarkStatusResponse> bookmarkBill(Long billId, @LoginUser Long userId) {
        return ResponseEntity.ok(billBookmarkAppService.bookmark(userId, billId));
    }

    @Override
    public ResponseEntity<BillBookmarkStatusResponse> unbookmarkBill(Long billId, @LoginUser Long userId) {
        return ResponseEntity.ok(billBookmarkAppService.unbookmark(userId, billId));
    }

    @Override
    public ResponseEntity<BillVoteSummaryResponse> getBillVoteSummary(Long billId, @LoginUser Long userId) {
        return ResponseEntity.ok(billDetailQueryService.getBillVoteSummary(billId, userId));
    }

    @Override
    public ResponseEntity<List<SimilarTopicBillResponse>> getSimilarTopics(
            Long billId,
            @LoginUser Long userId,
            SimilarTopicSortType sortType,
            Integer size
    ) {
        return ResponseEntity.ok(billDetailQueryService.getSimilarTopics(billId, userId, sortType, size));
    }

    @Override
    public ResponseEntity<List<BillStatusHistoryResponse>> getBillStatusHistory(Long billId, @LoginUser Long userId) {
        return ResponseEntity.ok(billDetailQueryService.getBillStatusHistory(billId, userId));
    }
}
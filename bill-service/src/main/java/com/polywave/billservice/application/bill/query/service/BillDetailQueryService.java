package com.polywave.billservice.application.bill.query.service;

import com.polywave.billservice.api.dto.BillDetailResponse;
import com.polywave.billservice.api.dto.BillStatusHistoryResponse;
import com.polywave.billservice.api.dto.BillVoteSummaryResponse;
import com.polywave.billservice.api.dto.SimilarTopicBillResponse;
import com.polywave.billservice.api.dto.SimilarTopicSortType;
import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import com.polywave.billservice.application.bill.query.result.BillStatusHistoryResult;
import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import com.polywave.billservice.application.bill.query.result.SimilarTopicBillResult;
import com.polywave.billservice.common.exception.BillNotFoundException;
import com.polywave.billservice.config.AgendaProperties;
import com.polywave.billservice.repository.query.BillDetailQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillDetailQueryService {

    private static final int MONTHLY_POPULAR_DAYS = 30;

    private final BillDetailQueryRepository billDetailQueryRepository;
    private final AgendaProperties agendaProperties;

    public BillDetailResponse getBillDetail(Long billId, Long userId) {
        BillDetailResult detail = billDetailQueryRepository.findBillDetailById(billId)
                .orElseThrow(BillNotFoundException::new);

        List<BillCategoryResult> categories = billDetailQueryRepository.findCategoriesByBillId(billId);
        BillVoteSummaryResult voteSummary = billDetailQueryRepository.findVoteSummaryByBillId(billId, userId);

        return BillDetailResponse.from(detail, categories, voteSummary);
    }

    public BillVoteSummaryResponse getBillVoteSummary(Long billId, Long userId) {
        validateBillExists(billId);
        BillVoteSummaryResult result = billDetailQueryRepository.findVoteSummaryByBillId(billId, userId);
        return BillVoteSummaryResponse.from(result);
    }

    public List<SimilarTopicBillResponse> getSimilarTopics(
            Long billId,
            Long userId,
            SimilarTopicSortType sortType,
            Integer size
    ) {
        validateBillExists(billId);

        int limitedSize = normalizeSize(size);
        SimilarTopicSortType effectiveSortType = sortType == null ? SimilarTopicSortType.RELEVANT : sortType;

        List<SimilarTopicBillResult> results = switch (effectiveSortType) {
            case RELEVANT -> billDetailQueryRepository.findSimilarTopicsByBillId(billId, limitedSize);
            case HOT_DEBATE -> billDetailQueryRepository.findHotDebateSimilarTopicsByBillId(
                    billId,
                    agendaProperties.hotDebate().days(),
                    agendaProperties.hotDebate().minVoteCount(),
                    limitedSize
            );
            case TRENDING -> billDetailQueryRepository.findTrendingSimilarTopicsByBillId(billId, limitedSize);
            case MONTHLY_POPULAR -> billDetailQueryRepository.findMonthlyPopularSimilarTopicsByBillId(
                    billId,
                    MONTHLY_POPULAR_DAYS,
                    limitedSize
            );
        };

        return results.stream()
                .map(SimilarTopicBillResponse::from)
                .toList();
    }

    public List<BillStatusHistoryResponse> getBillStatusHistory(Long billId, Long userId) {
        validateBillExists(billId);

        List<BillStatusHistoryResult> results = billDetailQueryRepository.findStatusHistoryByBillId(billId);
        return results.stream()
                .map(BillStatusHistoryResponse::from)
                .toList();
    }

    private void validateBillExists(Long billId) {
        if (!billDetailQueryRepository.existsBillById(billId)) {
            throw new BillNotFoundException();
        }
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return 5;
        }
        return Math.min(size, 20);
    }
}
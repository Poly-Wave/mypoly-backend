package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.bill.query.result.BillCategoryResult;
import com.polywave.billservice.application.bill.query.result.CoProposerResult;
import com.polywave.billservice.application.bill.query.result.BillDetailResult;
import com.polywave.billservice.application.bill.query.result.BillStatusHistoryResult;
import com.polywave.billservice.application.bill.query.result.BillVoteDetailResult;
import com.polywave.billservice.application.bill.query.result.BillVoteSummaryResult;
import com.polywave.billservice.application.bill.query.result.SimilarTopicBillResult;
import java.util.List;
import java.util.Optional;

public interface BillDetailQueryRepository {

    Optional<BillDetailResult> findBillDetailById(Long billId);

    List<BillCategoryResult> findCategoriesByBillId(Long billId);

    List<CoProposerResult> findCoProposersByBillId(Long billId);

    BillVoteSummaryResult findVoteSummaryByBillId(Long billId, Long userId);

    BillVoteDetailResult findVoteDetailByBillId(Long billId);

    List<SimilarTopicBillResult> findSimilarTopicsByBillId(Long billId, int size);

    List<SimilarTopicBillResult> findHotDebateSimilarTopicsByBillId(Long billId, int days, int minVoteCount, int size);

    List<SimilarTopicBillResult> findTrendingSimilarTopicsByBillId(Long billId, int size);

    List<SimilarTopicBillResult> findMonthlyPopularSimilarTopicsByBillId(Long billId, int days, int size);

    List<BillStatusHistoryResult> findStatusHistoryByBillId(Long billId);

    boolean existsBillById(Long billId);
}
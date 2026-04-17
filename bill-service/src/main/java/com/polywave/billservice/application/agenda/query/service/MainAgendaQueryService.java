package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.MainAgendaResult;
import com.polywave.billservice.application.category.query.service.UserBillInterestQueryService;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainAgendaQueryService {

    private static final String ICON_PREFIX = "bill-categories";

    private final AgendaQueryRepository agendaQueryRepository;
    private final UserBillInterestQueryService userBillInterestQueryService;

    @Value("${bill.category.icon-base-url}")
    private String iconBaseUrl;

    public List<MainAgendaResult> getMainAgendas(
            Long userId,
            boolean aiRecommended,
            Pageable pageable
    ) {
        Set<Long> interestCategoryIds = Set.of();
        boolean applyInterestFilter = false;

        if (aiRecommended) {
            interestCategoryIds = userBillInterestQueryService.getCurrentCategoryIds(userId);
            applyInterestFilter = !interestCategoryIds.isEmpty();
        }

        List<MainAgendaResult> rawResults = agendaQueryRepository.findMainAgendas(
                userId,
                applyInterestFilter,
                interestCategoryIds,
                pageable
        );

        return rawResults.stream()
                .map(result -> new MainAgendaResult(
                        result.officialTitle(),
                        result.summary(),
                        toCategoryIconUrl(result.categoryCode()),
                        result.proposalDate(),
                        result.viewCount(),
                        result.voteCount(),
                        result.categoryCode(),
                        result.categoryName(),
                        result.categoryBackgroundColor()
                ))
                .toList();
    }

    private String toCategoryIconUrl(String categoryCode) {
        if (categoryCode == null) {
            return null;
        }
        return iconBaseUrl + "/" + ICON_PREFIX + "/" + categoryCode + ".webp";
    }
}

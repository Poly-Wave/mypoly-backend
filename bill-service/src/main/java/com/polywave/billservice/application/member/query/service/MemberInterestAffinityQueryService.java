package com.polywave.billservice.application.member.query.service;

import com.polywave.billservice.application.member.query.result.CategoryCountResult;
import com.polywave.billservice.application.member.query.result.MemberCategoryRatioResult;
import com.polywave.billservice.application.member.query.result.MemberInterestAffinityResult;
import com.polywave.billservice.repository.query.MemberInterestAffinityQueryRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 의원의 발의 카테고리 분포와 사용자의 투표 카테고리 분포를 비교해 관심사 일치도를 계산한다.
 *
 * 계산식: 일치도 = SUM( MIN(의원의 카테고리별 비율, 사용자의 카테고리별 비율) )
 * - 두 분포 모두 합이 1.0 이므로 일치도는 0.0 ~ 1.0 범위가 된다.
 * - 예) 부동산 의원 0.4 vs 사용자 0.5 → 0.4, 의료 0.3 vs 0.5 → 0.3, 노동 0.2 vs 0 → 0
 *   → 일치도 0.7
 *
 * 분모(전체 건수)는 "대표 카테고리가 있는 건수" 기준이다. AI 분석이 없어 카테고리가 붙지 않은
 * 의안은 어느 카테고리에도 속하지 않아 비율을 왜곡시키므로 분자/분모 양쪽에서 제외한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInterestAffinityQueryService {

    /** 화면(버블 차트)에 노출할 의원 관심 카테고리 수. */
    private static final int TOP_CATEGORY_LIMIT = 4;

    private final MemberInterestAffinityQueryRepository memberInterestAffinityQueryRepository;

    public MemberInterestAffinityResult getInterestAffinity(Long memberId, Long userId) {
        List<CategoryCountResult> memberCounts =
                memberInterestAffinityQueryRepository.findMemberProposalCountsByCategory(memberId);
        if (memberCounts.isEmpty()) {
            // 의원 분포가 비면 사용자 분포와 무관하게 모든 MIN 값이 0 이므로 투표 집계 쿼리를 생략한다.
            return new MemberInterestAffinityResult(0.0, List.of());
        }

        List<CategoryCountResult> userCounts =
                memberInterestAffinityQueryRepository.findUserVoteCountsByCategory(userId);

        Map<Long, Double> memberRatios = toRatioByCategoryId(memberCounts);
        Map<Long, Double> userRatios = toRatioByCategoryId(userCounts);

        return new MemberInterestAffinityResult(
                calculateAffinityRate(memberRatios, userRatios),
                toTopCategories(memberCounts)
        );
    }

    /**
     * 발의 이력이 없는 의원, 투표 이력이 없는 사용자는 비어 있는 분포가 되어 일치도가 0.0 이 된다.
     */
    private Map<Long, Double> toRatioByCategoryId(List<CategoryCountResult> counts) {
        long total = totalCount(counts);
        if (total <= 0) {
            return Map.of();
        }

        Map<Long, Double> ratios = new HashMap<>();
        for (CategoryCountResult count : counts) {
            ratios.put(count.categoryId(), (double) count.count() / total);
        }
        return ratios;
    }

    private double calculateAffinityRate(Map<Long, Double> memberRatios, Map<Long, Double> userRatios) {
        double affinityRate = 0.0;
        for (Map.Entry<Long, Double> memberRatio : memberRatios.entrySet()) {
            double userRatio = userRatios.getOrDefault(memberRatio.getKey(), 0.0);
            affinityRate += Math.min(memberRatio.getValue(), userRatio);
        }
        return round(affinityRate);
    }

    private List<MemberCategoryRatioResult> toTopCategories(List<CategoryCountResult> memberCounts) {
        long total = totalCount(memberCounts);
        if (total <= 0) {
            return List.of();
        }

        return memberCounts.stream()
                // 비율이 같으면 카테고리 정의 순서(id)로 고정해 응답 순서가 흔들리지 않게 한다.
                .sorted(Comparator.comparingLong(CategoryCountResult::count).reversed()
                        .thenComparing(CategoryCountResult::categoryId))
                .limit(TOP_CATEGORY_LIMIT)
                .map(count -> new MemberCategoryRatioResult(
                        count.categoryCode(),
                        count.categoryName(),
                        count.backgroundColor(),
                        round((double) count.count() / total)
                ))
                .toList();
    }

    private long totalCount(List<CategoryCountResult> counts) {
        return counts.stream().mapToLong(CategoryCountResult::count).sum();
    }

    /** 부동소수점 누적 오차로 1.0 을 넘지 않도록 잘라내고 소수점 3자리로 반올림한다. */
    private double round(double value) {
        double bounded = Math.min(Math.max(value, 0.0), 1.0);
        return Math.round(bounded * 1000) / 1000.0;
    }
}

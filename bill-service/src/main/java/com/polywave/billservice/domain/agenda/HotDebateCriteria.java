package com.polywave.billservice.domain.agenda;

/**
 * "쟁쟁한" 안건(찬반이 팽팽한 의안) 판별 기준.
 * 찬성/반대 비율이 50:50에 가까울수록 상위 노출.
 */
public final class HotDebateCriteria {

    /** 찬성으로 집계할 vote_result 값 */
    public static final String VOTE_RESULT_AGREE = "찬성";

    /** 반대로 집계할 vote_result 값 */
    public static final String VOTE_RESULT_DISAGREE = "반대";

    /** 쟁쟁한으로 보려면 필요한 최소 투표 수 */
    public static final int MIN_VOTE_COUNT = 100;

    /** 찬성 비율 하한 (이 구간이면 팽팽한 것으로 봄) */
    public static final double MIN_AGREE_RATIO = 0.40;

    /** 찬성 비율 상한 */
    public static final double MAX_AGREE_RATIO = 0.60;

    private HotDebateCriteria() {
    }

    /**
     * 찬반 비율이 팽팽한지 여부.
     * |찬성% - 반대%|가 작을수록 true에 가깝게 쓰려면 별도 점수로 정렬하는 것이 맞고,
     * 여기서는 최소 투표 수와 비율 구간만 검사.
     */
    public static boolean isControversial(long agreeCount, long disagreeCount) {
        long total = agreeCount + disagreeCount;
        if (total < MIN_VOTE_COUNT) {
            return false;
        }
        double agreeRatio = (double) agreeCount / total;
        return agreeRatio >= MIN_AGREE_RATIO && agreeRatio <= MAX_AGREE_RATIO;
    }

    /**
     * 팽팽함 점수. 0에 가까울수록 더 쟁쟁함.
     * 정렬 시 이 값 오름차순 = |찬성% - 반대%|가 작은 순.
     */
    public static double controversyScore(double agreeRatio) {
        return Math.abs(agreeRatio - 0.5);
    }
}

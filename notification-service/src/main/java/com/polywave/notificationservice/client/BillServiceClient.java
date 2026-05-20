package com.polywave.notificationservice.client;

import com.polywave.notificationservice.client.dto.BillStageChangeDto;
import com.polywave.notificationservice.client.dto.BookmarkedUnvotedBillDto;
import com.polywave.notificationservice.client.dto.UserInterestAgendaCountDto;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * bill-service 의 internal segment API 호출 클라이언트.
 *
 * - X-Internal-Api-Key 헤더로 인증.
 * - 키가 비어 있으면 호출 자체를 시도하지 않고 빈 결과를 반환한다 (스케줄러가 운영 사고 없이 no-op 으로 동작).
 */
@Slf4j
@Component
public class BillServiceClient {

    private static final String BOOKMARKED_UNVOTED_PATH = "/internal/segments/bookmarked-unvoted";
    private static final String INTEREST_MATCHED_COUNTS_PATH = "/internal/segments/interest-matched-counts";
    private static final String BOOKMARKED_STAGE_CHANGES_PATH = "/internal/segments/bookmarked-stage-changes";

    private final RestTemplate restTemplate;
    private final String billServiceUrl;
    private final String internalApiKey;

    public BillServiceClient(
            RestTemplate restTemplate,
            @Value("${bill-service.url}") String billServiceUrl,
            @Value("${bill-service.internal-api.key:}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.billServiceUrl = stripTrailingSlash(billServiceUrl);
        this.internalApiKey = internalApiKey;
    }

    /**
     * 행 7: 북마크 D+1 미투표 (user, bill) 쌍 조회.
     */
    public List<BookmarkedUnvotedBillDto> findBookmarkedUnvotedBefore(Instant before) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            log.warn("bill-service.internal-api.key 가 비어있어 bookmarked-unvoted 호출을 건너뜁니다.");
            return List.of();
        }

        String url = UriComponentsBuilder.fromHttpUrl(billServiceUrl + BOOKMARKED_UNVOTED_PATH)
                .queryParam("before", before.toString())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            BookmarkedUnvotedBillDto[] response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, BookmarkedUnvotedBillDto[].class)
                    .getBody();

            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception e) {
            log.error("bill-service bookmarked-unvoted 호출 실패. url={}", url, e);
            return List.of();
        }
    }

    /**
     * 행 3: 관심 카테고리 매칭 신규 안건 카운트.
     */
    public List<UserInterestAgendaCountDto> findInterestMatchedCountsBetween(Instant from, Instant to) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            log.warn("bill-service.internal-api.key 가 비어있어 interest-matched-counts 호출을 건너뜁니다.");
            return List.of();
        }

        String url = UriComponentsBuilder.fromHttpUrl(billServiceUrl + INTEREST_MATCHED_COUNTS_PATH)
                .queryParam("from", from.toString())
                .queryParam("to", to.toString())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            UserInterestAgendaCountDto[] response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, UserInterestAgendaCountDto[].class)
                    .getBody();
            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception e) {
            log.error("bill-service interest-matched-counts 호출 실패. url={}", url, e);
            return List.of();
        }
    }

    /**
     * 행 6: 북마크된 의안의 최근 단계 전이 조회.
     */
    public List<BillStageChangeDto> findBookmarkedStageChangesSince(Instant since) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            log.warn("bill-service.internal-api.key 가 비어있어 bookmarked-stage-changes 호출을 건너뜁니다.");
            return List.of();
        }

        String url = UriComponentsBuilder.fromHttpUrl(billServiceUrl + BOOKMARKED_STAGE_CHANGES_PATH)
                .queryParam("since", since.toString())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            BillStageChangeDto[] response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, BillStageChangeDto[].class)
                    .getBody();
            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception e) {
            log.error("bill-service bookmarked-stage-changes 호출 실패. url={}", url, e);
            return List.of();
        }
    }

    private static String stripTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}

package com.polywave.notificationservice.client;

import com.polywave.notificationservice.client.dto.OnboardingReminderUserDto;
import com.polywave.notificationservice.client.dto.UserNicknameDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * user-service 의 internal segment API 호출 클라이언트.
 *
 * - X-Internal-Api-Key 헤더로 인증.
 * - 키가 비어 있으면 호출 자체를 시도하지 않고 빈 결과를 반환한다 (스케줄러가 운영 사고 없이 no-op 으로 동작).
 */
@Slf4j
@Component
public class UserServiceClient {

    private static final String SEGMENT_PATH = "/internal/segments/onboarding-reminder";
    private static final String ONBOARDING_COMPLETED_PATH = "/internal/segments/onboarding-completed";
    private static final String BY_IDS_PATH = "/internal/lookup/by-ids";

    // by-ids 는 GET query 라 URL 길이 제한 회피를 위해 이 단위로 나눠 호출한다.
    private static final int BY_IDS_BATCH_SIZE = 100;

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final String internalApiKey;

    public UserServiceClient(
            RestTemplate restTemplate,
            @Value("${user-service.url}") String userServiceUrl,
            @Value("${user-service.internal-api.key:}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = stripTrailingSlash(userServiceUrl);
        this.internalApiKey = internalApiKey;
    }

    public List<OnboardingReminderUserDto> findOnboardingReminderTargets(SegmentType type, Instant before) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            log.warn("user-service.internal-api.key 가 비어있어 onboarding reminder 호출을 건너뜁니다.");
            return List.of();
        }

        String url = UriComponentsBuilder.fromHttpUrl(userServiceUrl + SEGMENT_PATH)
                .queryParam("type", type.name())
                .queryParam("before", before.toString())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            OnboardingReminderUserDto[] response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, OnboardingReminderUserDto[].class)
                    .getBody();

            if (response == null) {
                return List.of();
            }
            return Arrays.asList(response);
        } catch (Exception e) {
            log.error("user-service segment 호출 실패. url={}, type={}, before={}", url, type, before, e);
            return List.of();
        }
    }

    /**
     * 관심 주제 선택 + 추가 정보 입력까지 완료한 유저 전원 조회.
     * 행 3·4·5 의 fan-out broadcast 대상.
     */
    public List<OnboardingReminderUserDto> findOnboardingCompletedUsers() {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            log.warn("user-service.internal-api.key 가 비어있어 onboarding-completed 호출을 건너뜁니다.");
            return List.of();
        }

        String url = userServiceUrl + ONBOARDING_COMPLETED_PATH;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            OnboardingReminderUserDto[] response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, OnboardingReminderUserDto[].class)
                    .getBody();

            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception e) {
            log.error("user-service onboarding-completed 호출 실패. url={}", url, e);
            return List.of();
        }
    }

    /**
     * 사용자 닉네임 일괄 조회 (행 3 본문 변수 치환 등에 사용).
     * - ids 를 GET query 로 넘기므로 URL 길이 제한을 피하기 위해 BATCH_SIZE 단위로 나눠 호출한다.
     */
    public List<UserNicknameDto> findNicknamesByIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        if (internalApiKey == null || internalApiKey.isBlank()) {
            log.warn("user-service.internal-api.key 가 비어있어 by-ids 호출을 건너뜁니다.");
            return List.of();
        }

        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(userIds));
        List<UserNicknameDto> result = new ArrayList<>(distinctIds.size());
        for (int from = 0; from < distinctIds.size(); from += BY_IDS_BATCH_SIZE) {
            int to = Math.min(from + BY_IDS_BATCH_SIZE, distinctIds.size());
            result.addAll(fetchNicknamesBatch(distinctIds.subList(from, to)));
        }
        return result;
    }

    private List<UserNicknameDto> fetchNicknamesBatch(List<Long> batchIds) {
        String idsParam = batchIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = UriComponentsBuilder.fromHttpUrl(userServiceUrl + BY_IDS_PATH)
                .queryParam("ids", idsParam)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            UserNicknameDto[] response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, UserNicknameDto[].class)
                    .getBody();
            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception e) {
            log.error("user-service by-ids 호출 실패. batchSize={}", batchIds.size(), e);
            return List.of();
        }
    }

    private static String stripTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public enum SegmentType {
        NICKNAME,
        CATEGORY
    }
}

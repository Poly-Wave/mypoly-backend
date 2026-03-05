package com.polywave.userservice.application.terms.query.service;

import com.polywave.userservice.application.terms.query.result.TermsDetailResult;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.common.exception.TermsNotFoundException;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TermsQueryServiceTest {

    @Mock
    private TermsQueryRepository termsQueryRepository;

    @InjectMocks
    private TermsQueryService termsQueryService;

    @Test
    @DisplayName("최신 약관 목록 전체 조회")
    void getLatestTerms_Success() {
        // given
        TermsResult term1 = new TermsResult(1L, "SERVICE", "서비스 이용약관", 1, true, null);
        TermsResult term2 = new TermsResult(2L, "PRIVACY", "개인정보 처리방침", 1, true, null);
        given(termsQueryRepository.findLatestTerms()).willReturn(List.of(term1, term2));

        // when
        List<TermsResult> result = termsQueryService.getLatestTerms();

        // then
        assertThat(result).hasSize(2)
                .extracting("title")
                .containsExactly("서비스 이용약관", "개인정보 처리방침");
    }

    @Test
    @DisplayName("특정 약관 메타 데이터 조회 성공")
    void getTermsMeta_Success() {
        // given
        Long termsId = 1L;
        TermsResult metaResult = new TermsResult(termsId, "SERVICE", "서비스 이용약관", 1, true, null);
        given(termsQueryRepository.findTermsMetaById(termsId)).willReturn(Optional.of(metaResult));

        // when
        TermsResult result = termsQueryService.getTermsMeta(termsId);

        // then
        assertThat(result.id()).isEqualTo(termsId);
        assertThat(result.title()).isEqualTo("서비스 이용약관");
    }

    @Test
    @DisplayName("특정 약관 메타 데이터 조회 실패 - 약관이 없는 경우 TermsNotFoundException 발생")
    void getTermsMeta_Fail_NotFound() {
        // given
        Long termsId = 999L;
        given(termsQueryRepository.findTermsMetaById(termsId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> termsQueryService.getTermsMeta(termsId))
                .isInstanceOf(TermsNotFoundException.class);
    }

    @Test
    @DisplayName("약관 상세 조회 성공")
    void getTermsDetail_Success() {
        // given
        Long termsId = 1L;
        TermsDetailResult detailResult = new TermsDetailResult(termsId, "SERVICE", "이용약관", 1, true, "본문내용", null);
        given(termsQueryRepository.findTermsDetailById(termsId)).willReturn(Optional.of(detailResult));

        // when
        TermsDetailResult result = termsQueryService.getTermsDetail(termsId);

        // then
        assertThat(result.content()).isEqualTo("본문내용");
    }

    @Test
    @DisplayName("약관 HTML WebView 문자열 생성 검증")
    void getTermsHtml_Success() {
        // given
        Long termsId = 1L;
        TermsDetailResult detailResult = new TermsDetailResult(termsId, "SERVICE", "이용약관", 1, true, "<h1>본문</h1>",
                null);
        given(termsQueryRepository.findTermsDetailById(termsId)).willReturn(Optional.of(detailResult));

        // when
        String html = termsQueryService.getTermsHtml(termsId);

        // then
        assertThat(html).contains("<!doctype html>")
                .contains("<h1>본문</h1>");
    }

    @Test
    @DisplayName("본문(content)이 null인 약관 HTML 빈 문자열 대응 검증")
    void getTermsHtml_NullContent() {
        // given
        Long termsId = 1L;
        TermsDetailResult detailResult = new TermsDetailResult(termsId, "SERVICE", "null본문", 1, true, null, null);
        given(termsQueryRepository.findTermsDetailById(termsId)).willReturn(Optional.of(detailResult));

        // when
        String html = termsQueryService.getTermsHtml(termsId);

        // then
        assertThat(html).contains("<body>")
                .contains("</body>")
                .doesNotContain("null");
    }
}

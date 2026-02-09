package com.polywave.userservice.application.terms.query.service;

import com.polywave.userservice.api.dto.TermsDetailResponse;
import com.polywave.userservice.api.dto.TermsResponse;
import com.polywave.userservice.api.exception.TermsNotFoundException;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.repository.command.TermsCommandRepository;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsQueryService {

    private final TermsQueryRepository termsQueryRepository;
    private final TermsCommandRepository termsCommandRepository;

    public List<TermsResult> getLatestTerms() {
        return termsQueryRepository.findLatestTerms();
    }

    /**
     * (기존) 약관 상세 조회 - content 포함(JSON).
     * 필요하면 내부/관리자 용도로 남겨두고,
     * 앱(WebView)에는 /terms/{id}/html 쓰면 됨.
     */
    public TermsDetailResponse getTermsDetail(Long termsId) {
        Terms terms = termsCommandRepository.findById(termsId)
                .orElseThrow(() -> new TermsNotFoundException(termsId));
        return TermsDetailResponse.from(terms);
    }

    /**
     * /terms/{id} : 메타데이터만
     */
    public TermsResponse getTermsMeta(Long termsId) {
        Terms terms = termsCommandRepository.findById(termsId)
                .orElseThrow(() -> new TermsNotFoundException(termsId));
        return TermsResponse.from(terms);
    }

    /**
     * /terms/{id}/html : WebView 전용 HTML
     * - content를 <body>에 그대로 삽입
     * - 모바일(WebView)에서 바로 렌더링 가능
     */
    public String getTermsHtml(Long termsId, Locale locale) {
        Terms terms = termsCommandRepository.findById(termsId)
                .orElseThrow(() -> new TermsNotFoundException(termsId));

        String content = terms.getContent();
        if (content == null) content = "";

        return """
                <!doctype html>
                <html lang="ko">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>약관</title>
                  </head>
                  <body>
                """ + content + """
                  </body>
                </html>
                """;
    }

    /**
     * Controller에서 Locale을 따로 안 넘겨도 되도록 overload.
     * (현재는 한국어 HTML만 내려주므로 기본 Locale.KOREA 사용)
     */
    public String getTermsHtml(Long termsId) {
        return getTermsHtml(termsId, Locale.KOREA);
    }
}

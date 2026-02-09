package com.polywave.userservice.application.terms.query.service;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.common.exception.TermsNotFoundException;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.repository.command.TermsCommandRepository;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import java.util.List;
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
     * 약관 엔티티 조회 (content 포함)
     * - Controller에서 필요한 응답 DTO로 매핑해서 사용
     */
    public Terms getTerms(Long termsId) {
        return termsCommandRepository.findById(termsId)
                .orElseThrow(() -> new TermsNotFoundException(termsId));
    }

    /**
     * /terms/{id}/html : WebView 전용 HTML
     * - content를 <body>에 그대로 삽입
     */
    public String getTermsHtml(Long termsId) {
        Terms terms = getTerms(termsId);

        String content = terms.getContent();
        if (content == null) content = "";

        return """
                <!doctype html>
                <html>
                  <head>
                    <meta charset="utf-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1"/>
                    <title>약관</title>
                  </head>
                  <body>
                """ + content + """
                  </body>
                </html>
                """;
    }
}

package com.polywave.userservice.application.terms.query.service;

import com.polywave.userservice.application.terms.query.result.TermsDetailResult;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.common.exception.TermsNotFoundException;
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

  public List<TermsResult> getLatestTerms() {
    return termsQueryRepository.findLatestTerms();
  }

  /**
   * 약관 메타데이터 조회 (content 제외)
   * - 조회 계층에서는 Entity 반환을 지양하고 DTO(Result)로만 반환
   */
  public TermsResult getTermsMeta(Long termsId) {
    return termsQueryRepository.findTermsMetaById(termsId)
        .orElseThrow(() -> new TermsNotFoundException());
  }

  /**
   * 약관 상세 조회 (content 포함)
   * - WebView(/terms/{id}/html) 등 본문이 필요한 경우에만 사용
   */
  public TermsDetailResult getTermsDetail(Long termsId) {
    return termsQueryRepository.findTermsDetailById(termsId)
        .orElseThrow(() -> new TermsNotFoundException());
  }

  /**
   * /terms/{id}/html : WebView 전용 HTML
   * - content를 <body>에 그대로 삽입
   */
  public String getTermsHtml(Long termsId) {
    TermsDetailResult terms = getTermsDetail(termsId);

    String content = terms.content();
    if (content == null)
      content = "";

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

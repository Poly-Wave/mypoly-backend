package com.polywave.userservice.application.terms.query.service;

import com.polywave.userservice.api.dto.TermsDetailResponse;
import com.polywave.userservice.api.exception.TermsNotFoundException;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.repository.command.TermsCommandRepository;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TermsQueryService {
    private final TermsQueryRepository termsQueryRepository;
    private final TermsCommandRepository termsCommandRepository;

    /** 약관 목록(종류별 최신 버전) */
    public List<TermsResult> getTerms() {
        return termsQueryRepository.findLatestTerms();
    }

    /** 약관 상세(본문 포함) */
    public TermsDetailResponse getTermsDetail(Long termsId) {
        Terms terms = termsCommandRepository.findById(termsId)
                .orElseThrow(() -> new TermsNotFoundException(termsId));
        return TermsDetailResponse.from(terms);
    }
}

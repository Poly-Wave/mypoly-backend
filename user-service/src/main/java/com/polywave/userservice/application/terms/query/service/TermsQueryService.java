package com.polywave.userservice.application.terms.query.service;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.repository.query.TermsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsQueryService {
    private final TermsQueryRepository termsQueryRepository;

    public List<TermsResult> getTerms() {
        return termsQueryRepository.findLatestTerms();
    }
}
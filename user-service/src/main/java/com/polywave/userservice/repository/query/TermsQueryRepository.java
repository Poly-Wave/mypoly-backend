package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.terms.query.result.TermsDetailResult;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import java.util.List;
import java.util.Optional;

public interface TermsQueryRepository {
    List<TermsResult> findLatestTerms();

    Optional<TermsResult> findTermsMetaById(Long termsId);

    Optional<TermsDetailResult> findTermsDetailById(Long termsId);
}

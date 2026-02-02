package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.domain.Terms;
import java.util.List;

public interface TermsQueryRepository {
    List<TermsResult> findLatestTerms();
}

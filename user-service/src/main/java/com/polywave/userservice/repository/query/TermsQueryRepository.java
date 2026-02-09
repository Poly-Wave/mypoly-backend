package com.polywave.userservice.repository.query;

import com.polywave.userservice.application.terms.query.result.TermsResult;
import java.util.List;

public interface TermsQueryRepository {
    List<TermsResult> findLatestTerms();
}

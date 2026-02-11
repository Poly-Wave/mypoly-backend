package com.polywave.userservice.repository.command;

import com.polywave.userservice.domain.UserTerms;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsCommandRepository extends JpaRepository<UserTerms, Long> {

    Optional<UserTerms> findByUserIdAndTermsId(Long userId, Long termsId);

    List<UserTerms> findByUserIdAndTermsIdIn(Long userId, List<Long> termsIds);
}

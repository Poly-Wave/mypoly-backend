package com.polywave.userservice.repository.command;

import com.polywave.userservice.domain.UserTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsCommandRepository extends JpaRepository<UserTerms, Long> {
}

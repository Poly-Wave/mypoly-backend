package com.polywave.userservice.repository.command;

import com.polywave.userservice.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsCommandRepository extends JpaRepository<Terms, Long> {
}

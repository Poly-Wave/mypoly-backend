package com.polywave.userservice.repository;

import com.polywave.userservice.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}

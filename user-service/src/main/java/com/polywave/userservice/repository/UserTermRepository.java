package com.polywave.userservice.repository;

import com.polywave.userservice.domain.UserTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
}

package com.polywave.userservice.repository;

import com.polywave.userservice.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
}

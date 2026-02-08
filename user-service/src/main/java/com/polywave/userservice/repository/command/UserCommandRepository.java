package com.polywave.userservice.repository.command;

import com.polywave.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommandRepository extends JpaRepository<User, Long> {
}

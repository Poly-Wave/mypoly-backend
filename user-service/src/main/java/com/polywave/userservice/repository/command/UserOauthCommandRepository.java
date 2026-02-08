package com.polywave.userservice.repository.command;

import com.polywave.userservice.domain.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOauthCommandRepository extends JpaRepository<UserOauth, Long> {
}

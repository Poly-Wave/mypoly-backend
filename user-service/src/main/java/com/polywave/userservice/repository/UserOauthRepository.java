package com.polywave.userservice.repository;

import com.polywave.userservice.domain.UserOauth;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
    @EntityGraph(attributePaths = "user")
    Optional<UserOauth> findByProviderAndProviderUserId(String provider, String providerUserId);
}

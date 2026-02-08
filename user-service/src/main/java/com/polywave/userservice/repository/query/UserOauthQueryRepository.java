package com.polywave.userservice.repository.query;

import com.polywave.userservice.domain.UserOauth;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserOauthQueryRepository extends Repository<UserOauth, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<UserOauth> findByProviderAndProviderUserId(String provider, String providerUserId);
}

package com.polywave.userservice.repository.query;

public interface UserQueryRepository {

    boolean existsByNickname(String nickname);
}

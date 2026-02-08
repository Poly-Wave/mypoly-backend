package com.polywave.userservice.repository.query;

public interface NicknameForbiddenWordQueryRepository {
    boolean containsForbiddenWord(String nickname);
}

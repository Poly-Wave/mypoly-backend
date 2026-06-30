package com.polywave.userservice.repository.command;

import com.polywave.userservice.domain.UserTerms;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserTermsCommandRepository extends JpaRepository<UserTerms, Long> {

    Optional<UserTerms> findByUserIdAndTermsId(Long userId, Long termsId);

    List<UserTerms> findByUserIdAndTermsIdIn(Long userId, List<Long> termsIds);

    // 회원 탈퇴 시 약관 동의 내역 말소. (재가입 시 약관 재동의로 다시 채워진다)
    @Modifying
    @Query("DELETE FROM UserTerms ut WHERE ut.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}

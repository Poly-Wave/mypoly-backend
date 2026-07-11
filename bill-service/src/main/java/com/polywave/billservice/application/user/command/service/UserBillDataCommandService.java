package com.polywave.billservice.application.user.command.service;

import com.polywave.billservice.repository.command.UserBillBookmarkCommandRepository;
import com.polywave.billservice.repository.command.UserBillInterestCommandRepository;
import com.polywave.billservice.repository.command.UserBillViewCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 탈퇴 시 사용자의 의안 파생 데이터(북마크/관심사/조회 기록)를 일괄 삭제한다.
 *
 * <p>표결(user_bill_votes)은 표결 결과 보존을 위해 삭제하지 않는다.
 * (사고 방지를 위해 vote repository 는 의도적으로 주입하지 않는다.)
 * 멱등하게 동작한다 — 데이터가 없어도 0건 삭제로 정상 종료한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBillDataCommandService {

    private final UserBillBookmarkCommandRepository userBillBookmarkCommandRepository;
    private final UserBillInterestCommandRepository userBillInterestCommandRepository;
    private final UserBillViewCommandRepository userBillViewCommandRepository;

    @Transactional
    public void deleteAllByUser(Long userId) {
        int bookmarks = userBillBookmarkCommandRepository.deleteAllByUserId(userId);
        int interests = userBillInterestCommandRepository.deleteAllByUserId(userId);
        int views = userBillViewCommandRepository.deleteAllByUserId(userId);

        log.info("[WITHDRAW] 사용자 의안 데이터 삭제 userId={} bookmarks={} interests={} views={} (votes 보존)",
                userId, bookmarks, interests, views);
    }
}

package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService{

    private final UserRepository userRepository;

    @Override
    public boolean isNicknameAvailable(String nickname) {

//        TODO 금칙어 체크
//        if (nicknamePolicyService.isForbidden(nickname)) {
//            return false;
//        }

        //  중복 체크
        return !userRepository.existsByNickname(nickname);
    }
}

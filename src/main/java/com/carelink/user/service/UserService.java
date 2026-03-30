package com.carelink.user.service;

import com.carelink.user.model.UserEntity;
import com.carelink.user.model.UserSession;
import com.carelink.user.model.dto.UserCreateRequest;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사용자 생성 + 세션 발급 조율. 로그인 없이 최초 접속 시 한 번만 호출
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSessionService userSessionService;

    @Transactional
    public UserSession registerAndCreateSession(UserCreateRequest request) {
        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .nationality(request.getNationality())
                .language(request.getLanguage())
                .build();

        UserEntity savedUser = userRepository.save(user);
        return userSessionService.createSession(savedUser.getUserId());
    }
}

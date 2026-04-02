package com.carelink.user.service;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.entity.UserSession;
import com.carelink.user.entity.dto.UserCreateRequest;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSessionService userSessionService;

    @Transactional
    public UserSession registerAndCreateSession(UserCreateRequest request) {
        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .language(request.getLanguage())
                .build();

        UserEntity savedUser = userRepository.save(user);
        // 여기서 세션 생성 + Redis 저장이 한 번에 일어남
        return userSessionService.createSession(savedUser.getUserId());
    }

    @Transactional
    public UserSession loginAndCreateSession(String name) {
        // 1. DB에서 이름으로 유저 조회
        UserEntity user = userRepository.findByName(name)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        // 2. 세션 생성 및 Redis 저장 (createSession 메서드 하나로 해결)
        return userSessionService.createSession(user.getUserId());
    }
}
package com.carelink.global.init;

import com.carelink.user.entity.UserEntity;
import com.carelink.user.entity.UserSession;
import com.carelink.user.repository.UserRepository;
import com.carelink.user.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// 앱 시작 시 기본 유저 + 세션 자동 생성. 팀원 개발 편의용
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final Long DEFAULT_USER_ID = 1L;

    private final UserRepository userRepository;
    private final UserSessionService userSessionService;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("========================================");
            log.info("기본 유저 또는 다른 유저가 이미 존재합니다. 초기화 스킵.");
            log.info("========================================");
            return;
        }

        UserEntity defaultUser = UserEntity.builder()
                .name("테스트유저")
                .nationality("한국")
                .language("ko")
                .build();
        UserEntity savedUser = userRepository.save(defaultUser);

        UserSession session = userSessionService.createSession(savedUser.getUserId());

        log.info("========================================");
        log.info("기본 유저 생성 완료");
        log.info("userId    : {}", savedUser.getUserId());
        log.info("SESSION_ID: {}", session.getSessionId());
        log.info("========================================");
    }
}

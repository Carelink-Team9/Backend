package com.carelink.user.service;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.user.model.UserSession;
import com.carelink.user.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 세션 비즈니스 로직. Redis를 직접 모르고 Repository 인터페이스만 사용
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public UserSession createSession(Long userId) {
        UserSession session = UserSession.create(userId);
        userSessionRepository.save(session);
        return session;
    }

    public UserSession getSession(String sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RestApiException(ErrorCode.SESSION_NOT_FOUND));

        if (session.isExpired()) {
            userSessionRepository.deleteById(sessionId);
            throw new RestApiException(ErrorCode.SESSION_EXPIRED);
        }

        return session;
    }

    public void deleteSession(String sessionId) {
        userSessionRepository.deleteById(sessionId);
    }
}

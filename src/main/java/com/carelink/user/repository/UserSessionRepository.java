package com.carelink.user.repository;

import com.carelink.user.model.UserSession;

import java.util.Optional;

// 세션 저장소 인터페이스. Service는 Redis를 직접 모름
public interface UserSessionRepository {

    void save(UserSession session);

    Optional<UserSession> findById(String sessionId);

    void deleteById(String sessionId);
}

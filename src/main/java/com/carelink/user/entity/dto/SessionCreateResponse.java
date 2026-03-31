package com.carelink.user.entity.dto;

import com.carelink.user.entity.UserSession;
import lombok.Getter;

import java.time.LocalDateTime;

// 세션 생성/조회 응답 DTO
@Getter
public class SessionCreateResponse {

    private final String sessionId;
    private final Long userId;
    private final LocalDateTime expiresAt;

    private SessionCreateResponse(UserSession session) {
        this.sessionId = session.getSessionId();
        this.userId = session.getUserId();
        this.expiresAt = session.getExpiresAt();
    }

    public static SessionCreateResponse from(UserSession session) {
        return new SessionCreateResponse(session);
    }
}

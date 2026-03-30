package com.carelink.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

// 사용자 세션 도메인 객체. Redis에 JSON으로 저장됨
@Getter
@Builder
public class UserSession {

    // Jackson 역직렬화용 생성자
    @JsonCreator
    public UserSession(
            @JsonProperty("sessionId") String sessionId,
            @JsonProperty("userId") Long userId,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("expiresAt") LocalDateTime expiresAt
    ) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    private final String sessionId;
    private final Long userId;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public static final long SESSION_DURATION_DAYS = 7;

    public static UserSession create(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return UserSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .userId(userId)
                .createdAt(now)
                .expiresAt(now.plusDays(SESSION_DURATION_DAYS))
                .build();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}

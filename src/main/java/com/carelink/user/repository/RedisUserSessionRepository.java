package com.carelink.user.repository;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.user.entity.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

// UserSessionRepository의 Redis 구현체. Redis 세부사항은 여기에만 존재
@Repository
@RequiredArgsConstructor
public class RedisUserSessionRepository implements UserSessionRepository {

    private static final String SESSION_KEY_PREFIX = "session:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(UserSession session) {
        try {
            String key = SESSION_KEY_PREFIX + session.getSessionId();
            String value = objectMapper.writeValueAsString(session);
            Duration ttl = Duration.ofDays(UserSession.SESSION_DURATION_DAYS);
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (JacksonException e) {
            throw new RestApiException(ErrorCode.SESSION_SERIALIZATION_FAILED);
        }
    }

    @Override
    public Optional<UserSession> findById(String sessionId) {
        try {
            String key = SESSION_KEY_PREFIX + sessionId;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, UserSession.class));
        } catch (JacksonException e) {
            throw new RestApiException(ErrorCode.SESSION_SERIALIZATION_FAILED);
        }
    }

    @Override
    public void deleteById(String sessionId) {
        redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
    }
}

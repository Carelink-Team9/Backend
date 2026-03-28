package com.carelink.user.service;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.user.model.UserSession;
import com.carelink.user.repository.UserSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceTest {

    @InjectMocks
    private UserSessionService userSessionService;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Test
    @DisplayName("세션 생성 - userId로 세션 생성 후 저장")
    void 세션_생성_성공() {
        Long userId = 1L;

        UserSession result = userSessionService.createSession(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getSessionId()).isNotNull();
        assertThat(result.isExpired()).isFalse();
        then(userSessionRepository).should().save(any(UserSession.class));
    }

    @Test
    @DisplayName("세션 조회 - 유효한 세션 반환")
    void 세션_조회_성공() {
        UserSession session = UserSession.create(1L);
        given(userSessionRepository.findById(session.getSessionId())).willReturn(Optional.of(session));

        UserSession result = userSessionService.getSession(session.getSessionId());

        assertThat(result.getSessionId()).isEqualTo(session.getSessionId());
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("세션 조회 - 존재하지 않는 세션이면 SESSION_NOT_FOUND 예외")
    void 세션_조회_없는세션() {
        given(userSessionRepository.findById("없는세션")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userSessionService.getSession("없는세션"))
                .isInstanceOf(RestApiException.class)
                .satisfies(e -> assertThat(((RestApiException) e).getErrorCode())
                        .isEqualTo(ErrorCode.SESSION_NOT_FOUND));
    }

    @Test
    @DisplayName("세션 조회 - 만료된 세션이면 SESSION_EXPIRED 예외 + 세션 삭제")
    void 세션_조회_만료된세션() {
        UserSession expiredSession = UserSession.builder()
                .sessionId("만료세션")
                .userId(1L)
                .createdAt(LocalDateTime.now().minusDays(8))
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();
        given(userSessionRepository.findById("만료세션")).willReturn(Optional.of(expiredSession));

        assertThatThrownBy(() -> userSessionService.getSession("만료세션"))
                .isInstanceOf(RestApiException.class)
                .satisfies(e -> assertThat(((RestApiException) e).getErrorCode())
                        .isEqualTo(ErrorCode.SESSION_EXPIRED));

        then(userSessionRepository).should().deleteById("만료세션");
    }

    @Test
    @DisplayName("세션 삭제 - 저장소에서 세션 제거")
    void 세션_삭제_성공() {
        userSessionService.deleteSession("삭제할세션");

        then(userSessionRepository).should().deleteById("삭제할세션");
    }
}

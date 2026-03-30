package com.carelink.user.service;

import com.carelink.user.model.UserEntity;
import com.carelink.user.model.UserSession;
import com.carelink.user.model.dto.UserCreateRequest;
import com.carelink.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSessionService userSessionService;

    @Test
    @DisplayName("사용자 등록 + 세션 발급 - DB 저장 후 세션 생성")
    void 사용자_등록_및_세션_발급_성공() {
        UserCreateRequest request = createRequest("홍길동", "한국", "ko");

        UserEntity savedUser = UserEntity.builder()
                .name("홍길동")
                .nationality("한국")
                .language("ko")
                .build();
        setUserId(savedUser, 1L);

        UserSession expectedSession = UserSession.create(1L);

        given(userRepository.save(any(UserEntity.class))).willReturn(savedUser);
        given(userSessionService.createSession(1L)).willReturn(expectedSession);

        UserSession result = userService.registerAndCreateSession(request);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getSessionId()).isNotNull();
        then(userRepository).should().save(any(UserEntity.class));
        then(userSessionService).should().createSession(1L);
    }

    // UserCreateRequest 인스턴스 생성 헬퍼 (setter 없으므로 리플렉션 사용)
    private UserCreateRequest createRequest(String name, String nationality, String language) {
        try {
            UserCreateRequest request = new UserCreateRequest();
            setField(request, "name", name);
            setField(request, "nationality", nationality);
            setField(request, "language", language);
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void setUserId(UserEntity entity, Long id) {
        try {
            var field = UserEntity.class.getDeclaredField("userId");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

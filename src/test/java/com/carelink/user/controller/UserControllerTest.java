package com.carelink.user.controller;

import com.carelink.global.config.SecurityConfig;
import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.global.util.SessionCookieManager;
import com.carelink.user.entity.UserSession;
import com.carelink.user.service.UserService;
import com.carelink.user.service.UserSessionService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserSessionService userSessionService;

    @MockitoBean
    private SessionCookieManager sessionCookieManager;

    @Test
    @DisplayName("사용자 등록 - 정상 요청 시 200 + sessionId 반환")
    void 사용자_등록_성공() throws Exception {
        UserSession session = UserSession.create(1L);
        given(userService.registerAndCreateSession(any())).willReturn(session);
        willDoNothing().given(sessionCookieManager).set(any(), anyString());

        String body = """
                {
                    "name": "홍길동",
                    "nationality": "한국",
                    "language": "ko"
                }
                """;

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").value(session.getSessionId()))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    @DisplayName("사용자 등록 - 필수 필드 누락 시 400")
    void 사용자_등록_필드누락() throws Exception {
        String body = """
                {
                    "name": "",
                    "nationality": "한국",
                    "language": "ko"
                }
                """;

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세션 조회 - 유효한 쿠키로 200 반환")
    void 세션_조회_성공() throws Exception {
        UserSession session = UserSession.create(1L);
        given(sessionCookieManager.extract(any())).willReturn(Optional.of(session.getSessionId()));
        given(userSessionService.getSession(session.getSessionId())).willReturn(session);

        mockMvc.perform(get("/api/user/session")
                        .cookie(new Cookie("SESSION_ID", session.getSessionId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    @DisplayName("세션 조회 - 쿠키 없으면 401")
    void 세션_조회_쿠키없음() throws Exception {
        given(sessionCookieManager.extract(any())).willReturn(Optional.empty());

        mockMvc.perform(get("/api/user/session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("세션 조회 - 만료된 세션이면 401")
    void 세션_조회_만료() throws Exception {
        given(sessionCookieManager.extract(any())).willReturn(Optional.of("만료세션"));
        given(userSessionService.getSession("만료세션"))
                .willThrow(new RestApiException(ErrorCode.SESSION_EXPIRED));

        mockMvc.perform(get("/api/user/session")
                        .cookie(new Cookie("SESSION_ID", "만료세션")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("세션 삭제 - 정상 요청 시 200")
    void 세션_삭제_성공() throws Exception {
        UserSession session = UserSession.create(1L);
        given(sessionCookieManager.extract(any())).willReturn(Optional.of(session.getSessionId()));
        willDoNothing().given(userSessionService).deleteSession(anyString());
        willDoNothing().given(sessionCookieManager).expire(any());

        mockMvc.perform(delete("/api/user/session")
                        .cookie(new Cookie("SESSION_ID", session.getSessionId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

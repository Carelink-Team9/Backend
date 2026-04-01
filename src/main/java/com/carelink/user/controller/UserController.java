package com.carelink.user.controller;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.response.ApiResponse;
import com.carelink.global.type.ErrorCode;
import com.carelink.global.type.ResponseMessage;
import com.carelink.global.util.SessionCookieManager;
import com.carelink.user.entity.UserSession;
import com.carelink.user.entity.dto.SessionCreateResponse;
import com.carelink.user.entity.dto.UserCreateRequest;
import com.carelink.user.service.UserService;
import com.carelink.user.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSessionService userSessionService;
    private final SessionCookieManager sessionCookieManager;

    // 사용자 등록 + 세션 발급. 최초 접속 시 한 번만 호출
    @PostMapping
    public ResponseEntity<ApiResponse<SessionCreateResponse>> register(
            @RequestBody @Valid UserCreateRequest request,
            HttpServletResponse response
    ) {
        UserSession session = userService.registerAndCreateSession(request);
        sessionCookieManager.set(response, session.getSessionId());
        return ResponseEntity.ok(ApiResponse.ok(SessionCreateResponse.from(session)));
    }

    // 세션 조회. 재접속 시 쿠키로 세션 유효성 확인
    @GetMapping("/session")
    public ResponseEntity<ApiResponse<SessionCreateResponse>> getSession(HttpServletRequest request) {
        String sessionId = extractSessionId(request);
        UserSession session = userSessionService.getSession(sessionId);
        return ResponseEntity.ok(ApiResponse.ok(SessionCreateResponse.from(session)));
    }

    // 세션 삭제
    @DeleteMapping("/session")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String sessionId = extractSessionId(request);
        userSessionService.deleteSession(sessionId);
        sessionCookieManager.expire(response);
        return ResponseEntity.ok(ApiResponse.ok(ResponseMessage.LOGOUT_SUCCESS));
    }

    private String extractSessionId(HttpServletRequest request) {
        return sessionCookieManager.extract(request)
                .orElseThrow(() -> new RestApiException(ErrorCode.SESSION_NOT_FOUND));
    }
}

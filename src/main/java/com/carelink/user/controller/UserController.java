package com.carelink.user.controller;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.response.ApiResponse;
import com.carelink.global.type.ErrorCode;
import com.carelink.global.type.ResponseMessage;
import com.carelink.global.util.SessionCookieManager;
import com.carelink.user.entity.UserSession;
import com.carelink.user.entity.dto.LoginRequest;
import com.carelink.user.entity.dto.SessionCreateResponse;
import com.carelink.user.entity.dto.UserCreateRequest;
import com.carelink.user.service.UserService;
import com.carelink.user.service.UserSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 등록 및 세션 API")
public class UserController {

    private final UserService userService;
    private final UserSessionService userSessionService;
    private final SessionCookieManager sessionCookieManager;

    // 사용자 등록 + 세션 발급. 최초 접속 시 한 번만 호출
    @Operation(summary = "회원 등록", description = "사용자를 등록하고 세션 쿠키를 발급합니다.")
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
    @Operation(summary = "현재 세션 조회", description = "SESSION_ID 쿠키를 기준으로 현재 세션 정보를 조회합니다.")
    @GetMapping("/session")
    public ResponseEntity<ApiResponse<SessionCreateResponse>> getSession(HttpServletRequest request) {
        String sessionId = extractSessionId(request);
        UserSession session = userSessionService.getSession(sessionId);
        return ResponseEntity.ok(ApiResponse.ok(SessionCreateResponse.from(session)));
    }

    // 세션 삭제
    @Operation(summary = "로그아웃", description = "현재 세션을 삭제하고 세션 쿠키를 만료시킵니다.")
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


    // UserController.java 에 추가
    @Operation(summary = "로그인", description = "이름으로 기존 사용자를 로그인시키고 세션 쿠키를 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SessionCreateResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest, // 아래 DTO 생성 필요
            HttpServletResponse response
    ) {
        // 1. 이름으로 유저를 찾고 세션을 생성 (Service 로직)
        UserSession session = userService.loginAndCreateSession(loginRequest.getName());

        // 2. 쿠키에 세션 ID 심기
        sessionCookieManager.set(response, session.getSessionId());

        return ResponseEntity.ok(ApiResponse.ok(SessionCreateResponse.from(session)));
    }
}

package com.carelink.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

// 세션 쿠키 생성/조회/만료 전담 컴포넌트
@Component
public class SessionCookieManager {

    public static final String SESSION_COOKIE_NAME = "SESSION_ID";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7; // 7일 (초)

    public void set(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        cookie.setHttpOnly(true); // JS 접근 차단
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        // cookie.setSecure(true); // HTTPS 환경에서 활성화
        response.addCookie(cookie);
    }

    public void expire(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }

    public Optional<String> extract(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> SESSION_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}

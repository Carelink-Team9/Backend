package com.carelink.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 에러 코드 enum. RestApiException 발생 시 사용, 도메인별 에러는 여기에 추가
@Getter
@AllArgsConstructor
public enum ErrorCode {

    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 요청 포맷입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),

    // 세션
    SESSION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "세션이 존재하지 않습니다."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다."),
    SESSION_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "세션 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    public HttpStatus getHttpStatus() {
        return status;
    }

    public String getDescription() {
        return message;
    }
}

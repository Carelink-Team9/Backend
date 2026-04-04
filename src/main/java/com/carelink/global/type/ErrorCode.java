package com.carelink.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 가능한 파일 크기를 초과했습니다."),
    INVALID_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    SESSION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "세션이 존재하지 않습니다."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다."),
    SESSION_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "세션 처리 중 오류가 발생했습니다."),

    PRESCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "처방 정보를 찾을 수 없습니다."),
    DRUG_CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "약 카드 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    public HttpStatus getHttpStatus() {
        return status;
    }

    public String getDescription() {
        return message;
    }
}

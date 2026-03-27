package com.carelink.global.exception;

import com.carelink.global.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 커스텀 예외. 비즈니스 로직에서 throw new RestApiException(ErrorCode.XXX) 으로 사용
@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public RestApiException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }
}

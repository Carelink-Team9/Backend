package com.carelink.global.response;

import com.carelink.global.type.ErrorCode;
import com.carelink.global.type.ResponseMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공통 API 응답 포맷. 모든 컨트롤러 응답에 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // 응답 데이터만 반환할 때
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data);
    }

    // 응답 메시지 + 데이터 함께 반환할 때
    public static <T> ApiResponse<T> ok(ResponseMessage responseMessage, T data) {
        return new ApiResponse<>(true, responseMessage.getMessage(), data);
    }

    // 응답 메시지만 반환할 때 (데이터 없는 성공)
    public static ApiResponse<Void> ok(ResponseMessage responseMessage) {
        return new ApiResponse<>(true, responseMessage.getMessage(), null);
    }

    // 에러 코드만으로 실패 응답 반환할 때
    public static ApiResponse<Void> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getDescription(), null);
    }

    // 에러 코드 + 추가 데이터(ex. validation errors) 함께 반환할 때
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T data) {
        return new ApiResponse<>(false, errorCode.getDescription(), data);
    }
}

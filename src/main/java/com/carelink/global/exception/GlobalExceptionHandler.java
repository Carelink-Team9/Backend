package com.carelink.global.exception;

import com.carelink.global.response.ApiResponse;
import com.carelink.global.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

import static com.carelink.global.type.ErrorCode.BAD_REQUEST;
import static com.carelink.global.type.ErrorCode.FILE_TOO_LARGE;
import static com.carelink.global.type.ErrorCode.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("커스텀 예외 발생: errorCode={}, message={}", errorCode.name(), errorCode.getDescription());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e
    ) {
        log.error("유효성 검증 실패", e);
        List<ValidationError> validationErrors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(ValidationError::of)
                .collect(Collectors.toList());
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(BAD_REQUEST, validationErrors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("잘못된 인자 전달", e);
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(BAD_REQUEST));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("DB 제약 조건 위반", e);
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(BAD_REQUEST));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("업로드 파일 크기 초과", e);
        return ResponseEntity.status(FILE_TOO_LARGE.getHttpStatus())
                .body(ApiResponse.fail(FILE_TOO_LARGE));
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException e) {
        log.debug("클라이언트 연결 중단(무시): {}", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상치 못한 예외 발생", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.fail(INTERNAL_SERVER_ERROR));
    }
}

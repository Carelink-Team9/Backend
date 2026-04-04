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

import java.util.List;
import java.util.stream.Collectors;

import static com.carelink.global.type.ErrorCode.BAD_REQUEST;
import static com.carelink.global.type.ErrorCode.INTERNAL_SERVER_ERROR;

// 전역 예외 처리. 컨트롤러에서 발생하는 모든 예외를 여기서 처리
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 커스텀 예외 — throw new RestApiException(ErrorCode.XXX) 으로 발생
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("커스텀 예외 발생 : 에러코드 = {}, 메시지 = {}",
                errorCode.name(), errorCode.getDescription());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode));
    }

    // @Valid 검증 실패 — 어떤 필드가 왜 실패했는지 errors에 담아 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("유효성 검증 실패", e);
        List<ValidationError> validationErrors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(ValidationError::of)
                .collect(Collectors.toList());
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(BAD_REQUEST, validationErrors));
    }

    // 잘못된 파라미터 — 부적절한 인자 전달 시
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("잘못된 인자 전달", e);
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(BAD_REQUEST));
    }

    // DB 제약 조건 위반 — unique 제약, FK 제약 등
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("DB 제약 조건 위반", e);
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.fail(BAD_REQUEST));
    }

    // 클라이언트가 연결을 끊은 경우 — 이미지 전송 중 브라우저가 요청 취소할 때 발생, 무시
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException e) {
        log.debug("클라이언트 연결 중단 (무시): {}", e.getMessage());
    }

    // 그 외 모든 예외 — 위에서 잡히지 않은 예상치 못한 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상치 못한 예외 발생", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.fail(INTERNAL_SERVER_ERROR));
    }
}

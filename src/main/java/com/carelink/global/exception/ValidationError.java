package com.carelink.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

// @Valid 검증 실패 시 어떤 필드가 왜 실패했는지 담는 DTO
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationError {

    private final String field;
    private final String message;

    public static ValidationError of(FieldError fieldError) {
        return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
    }
}

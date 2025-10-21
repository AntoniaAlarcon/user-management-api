package com.antonia.dev.userapi.exception;

import com.antonia.dev.userapi.dto.common.ErrorResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class UserValidationException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public UserValidationException(List<ErrorResponse> errors) {
        super("Validation failed");
        this.errors = errors;
    }

}
package com.antonia.dev.userapi.exception;

import com.antonia.dev.userapi.dto.common.ErrorResponse;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String field;

    public UserNotFoundException(String field, String message) {
        super(message);
        this.field = field;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(field, getMessage());
    }
}

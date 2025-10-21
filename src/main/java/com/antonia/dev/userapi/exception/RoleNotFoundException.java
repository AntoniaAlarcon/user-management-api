package com.antonia.dev.userapi.exception;

import com.antonia.dev.userapi.dto.common.ErrorResponse;
import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private final String field;


    public RoleNotFoundException(String field, String message) {
        super(message);
        this.field = field;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(field, getMessage());
    }
}

package com.antonia.dev.userapi.handler;

import com.antonia.dev.userapi.dto.common.ErrorResponse;
import com.antonia.dev.userapi.exception.RoleNotFoundException;
import com.antonia.dev.userapi.exception.UserNotFoundException;
import com.antonia.dev.userapi.exception.UserValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleUserValidationException(UserValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", ex.getErrors()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("errors", List.of(ex.toErrorResponse())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("general", "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errors", List.of(error)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String field = ex.getName();
        String message = "Invalid value: " + ex.getValue();

        ErrorResponse error = new ErrorResponse(field, message);
        return ResponseEntity.badRequest().body(Map.of("errors", List.of(error)));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("errors", List.of(ex.toErrorResponse())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleBadCredentials(BadCredentialsException ex) {
        String message = messageSource.getMessage("auth.invalid.credentials", null, Locale.getDefault());
        ErrorResponse error = new ErrorResponse("login", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("errors", List.of(error)));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleDisabled(DisabledException ex) {
        String message = messageSource.getMessage("auth.account.disabled", null, Locale.getDefault());
        ErrorResponse error = new ErrorResponse("login", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("errors", List.of(error)));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleLocked(LockedException ex) {
        String message = messageSource.getMessage("auth.account.locked", null, Locale.getDefault());
        ErrorResponse error = new ErrorResponse("login", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("errors", List.of(error)));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleAuthenticationException(AuthenticationException ex) {
        String message = messageSource.getMessage("auth.processing.error", null, Locale.getDefault());
        ErrorResponse error = new ErrorResponse("login", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errors", List.of(error)));
    }
}
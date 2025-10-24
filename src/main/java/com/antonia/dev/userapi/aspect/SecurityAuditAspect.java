package com.antonia.dev.userapi.aspect;

import com.antonia.dev.userapi.dto.auth.LoginRequest;
import com.antonia.dev.userapi.dto.auth.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class SecurityAuditAspect {

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.controller.AuthController.login(..))",
        returning = "result"
    )
    public void auditSuccessfulLogin(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity<?> response) {
            if (response.getBody() instanceof LoginResponse loginResponse) {
                log.info("SECURITY [LOGIN_SUCCESS] - Username: {}, Role: {}, UserID: {}, Timestamp: {}", 
                         loginResponse.username(), 
                         loginResponse.role(), 
                         loginResponse.userId(),
                         LocalDateTime.now());
            }
        }
    }

    @AfterThrowing(
        pointcut = "execution(* com.antonia.dev.userapi.controller.AuthController.login(..))",
        throwing = "exception"
    )
    public void auditFailedLogin(JoinPoint joinPoint, Exception exception) {
        Object[] args = joinPoint.getArgs();
        
        if (args.length > 0 && args[0] instanceof LoginRequest loginRequest) {
            String reason = getFailureReason(exception);
            
            log.warn("SECURITY [LOGIN_FAILED] - Username: {}, Reason: {}, Timestamp: {}", 
                     loginRequest.username(), 
                     reason,
                     LocalDateTime.now());
        }
    }

    @Before("execution(* com.antonia.dev.userapi.controller.AuthController.validateToken(..))")
    public void auditTokenValidation(JoinPoint joinPoint) {
        log.debug("SECURITY [TOKEN_VALIDATION] - Validation attempt at {}", 
                  LocalDateTime.now());
    }

    // NOTE: This aspect is commented out because it causes issues with the initialization of the JWT filter.
    // AOP tries to create a proxy for the filter, which interferes with the logger initialization.
    // JWT filter errors are handled internally within the filter itself.
    /*
    @AfterThrowing(
        pointcut = "execution(* com.antonia.dev.userapi.security.JwtAuthenticationFilter.doFilterInternal(..))",
        throwing = "exception"
    )
    public void auditJwtFilterError(JoinPoint joinPoint, Exception exception) {
        log.error("SECURITY [JWT_FILTER_ERROR] - Error: {}, Timestamp: {}", 
                  exception.getMessage(),
                  LocalDateTime.now());
    }
    */

    @AfterReturning("execution(* com.antonia.dev.userapi.service.user.UserService.updateSelf(..)) && args(id, request)")
    public void auditPasswordChange(Long id, Object request) {
        log.info("SECURITY [PASSWORD_CHANGE_ATTEMPT] - UserID: {}, Timestamp: {}",
                 id,
                 LocalDateTime.now());
    }

    private String getFailureReason(Exception exception) {
        if (exception instanceof BadCredentialsException) {
            return "INVALID_CREDENTIALS";
        } else if (exception instanceof DisabledException) {
            return "ACCOUNT_DISABLED";
        } else if (exception instanceof LockedException) {
            return "ACCOUNT_LOCKED";
        } else {
            return "AUTHENTICATION_ERROR";
        }
    }
}

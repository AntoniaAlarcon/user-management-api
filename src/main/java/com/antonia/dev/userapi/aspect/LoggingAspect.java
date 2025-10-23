package com.antonia.dev.userapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.antonia.dev.userapi.service..*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("→ Executing: {}.{}() with arguments: {}", 
                 className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("✓ Completed: {}.{}() in {} ms", 
                     className, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.error("✗ Failed: {}.{}() after {} ms - Error: {}", 
                      className, methodName, executionTime, e.getMessage());
            
            throw e;
        }
    }

    @Around("execution(* com.antonia.dev.userapi.controller..*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        
        log.debug("HTTP Request received: {}", methodName);
        
        Object result = joinPoint.proceed();
        
        log.debug("HTTP Response sent: {}", methodName);
        
        return result;
    }
}


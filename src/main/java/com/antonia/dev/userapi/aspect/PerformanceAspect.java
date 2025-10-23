package com.antonia.dev.userapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("!test")
@Slf4j
public class PerformanceAspect {

    private static final long SLOW_QUERY_THRESHOLD_MS = 1000; // 1 second
    private static final long SLOW_SERVICE_THRESHOLD_MS = 2000; // 2 seconds

    @Around("execution(* com.antonia.dev.userapi.repository..*.*(..))")
    public Object monitorRepositoryPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > SLOW_QUERY_THRESHOLD_MS) {
                log.warn("PERFORMANCE [SLOW_QUERY] - Method: {} took {} ms (threshold: {} ms)", 
                         methodName, executionTime, SLOW_QUERY_THRESHOLD_MS);
            } else {
                log.debug("PERFORMANCE [QUERY] - Method: {} took {} ms", 
                          methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("PERFORMANCE [QUERY_FAILED] - Method: {} failed after {} ms", 
                      methodName, executionTime);
            throw e;
        }
    }

    @Around("execution(* com.antonia.dev.userapi.service..*.*(..))")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > SLOW_SERVICE_THRESHOLD_MS) {
                log.warn("PERFORMANCE [SLOW_SERVICE] - Method: {} took {} ms (threshold: {} ms)", 
                         methodName, executionTime, SLOW_SERVICE_THRESHOLD_MS);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("PERFORMANCE [SERVICE_FAILED] - Method: {} failed after {} ms", 
                      methodName, executionTime);
            throw e;
        }
    }

    @Around("execution(* com.antonia.dev.userapi.controller..*.*(..))")
    public Object monitorEndpointPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String endpoint = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("PERFORMANCE [ENDPOINT] - {} responded in {} ms", 
                     endpoint, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("PERFORMANCE [ENDPOINT_FAILED] - {} failed after {} ms", 
                      endpoint, executionTime);
            throw e;
        }
    }
}


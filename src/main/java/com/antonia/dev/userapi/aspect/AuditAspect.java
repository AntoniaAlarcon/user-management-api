package com.antonia.dev.userapi.aspect;

import com.antonia.dev.userapi.dto.role.RoleDTO;
import com.antonia.dev.userapi.dto.user.UserDTO;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.user.UserService.createUser(..))",
        returning = "result"
    )
    public void auditUserCreation(JoinPoint joinPoint, UserDTO result) {
        String performedBy = getCurrentUsername();
        
        log.info("AUDIT [USER_CREATED] - ID: {}, Username: {}, Email: {}, Role: {}, Created by: {}, Timestamp: {}", 
                 result.id(), 
                 result.username(), 
                 result.email(), 
                 result.roleName(),
                 performedBy,
                 LocalDateTime.now());
    }

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.user.UserService.updateByAdmin(..))",
        returning = "result"
    )
    public void auditUserUpdate(JoinPoint joinPoint, Optional<UserDTO> result) {
        String performedBy = getCurrentUsername();
        
        result.ifPresent(user -> 
            log.info("AUDIT [USER_UPDATED] - ID: {}, Username: {}, Modified by: {}, Timestamp: {}", 
                     user.id(), 
                     user.username(), 
                     performedBy,
                     LocalDateTime.now())
        );
    }

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.user.UserService.updateSelf(..))",
        returning = "result"
    )
    public void auditUserSelfUpdate(JoinPoint joinPoint, Optional<UserDTO> result) {
        String performedBy = getCurrentUsername();
        
        result.ifPresent(user -> 
            log.info("AUDIT [USER_SELF_UPDATED] - ID: {}, Username: {}, Modified by: {}, Timestamp: {}", 
                     user.id(), 
                     user.username(), 
                     performedBy,
                     LocalDateTime.now())
        );
    }

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.user.UserService.delete(..))",
        returning = "result"
    )
    public void auditUserDeletion(JoinPoint joinPoint, Optional<User> result) {
        String performedBy = getCurrentUsername();
        
        result.ifPresent(user -> 
            log.warn("AUDIT [USER_DELETED] - ID: {}, Username: {}, Email: {}, Deleted by: {}, Timestamp: {}", 
                     user.getId(), 
                     user.getUsername(), 
                     user.getEmail(),
                     performedBy,
                     LocalDateTime.now())
        );
    }

    @AfterThrowing(
        pointcut = "execution(* com.antonia.dev.userapi.service.user.UserService.*(..))",
        throwing = "exception"
    )
    public void auditFailedOperation(JoinPoint joinPoint, Exception exception) {
        String performedBy = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("AUDIT [OPERATION_FAILED] - Method: {}, User: {}, Error: {}, Timestamp: {}", 
                  methodName, 
                  performedBy, 
                  exception.getMessage(),
                  LocalDateTime.now());
    }

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.role.RoleService.createRole(..))",
        returning = "result"
    )
    public void auditRoleCreation(JoinPoint joinPoint, RoleDTO result) {
        String performedBy = getCurrentUsername();
        String userRole = getCurrentUserRole();
        
        log.info("AUDIT [ROLE_CREATED] - ID: {}, Name: {}, Description: {}, Created by: {} (Role: {}), Timestamp: {}", 
                 result.id(), 
                 result.name(), 
                 result.description(),
                 performedBy,
                 userRole,
                 LocalDateTime.now());
    }

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.role.RoleService.updateRole(..))",
        returning = "result"
    )
    public void auditRoleUpdate(JoinPoint joinPoint, Optional<RoleDTO> result) {
        String performedBy = getCurrentUsername();
        String userRole = getCurrentUserRole();
        
        result.ifPresent(role -> 
            log.info("AUDIT [ROLE_UPDATED] - ID: {}, Name: {}, Description: {}, Modified by: {} (Role: {}), Timestamp: {}", 
                     role.id(), 
                     role.name(), 
                     role.description(),
                     performedBy,
                     userRole,
                     LocalDateTime.now())
        );
    }

    @AfterReturning(
        pointcut = "execution(* com.antonia.dev.userapi.service.role.RoleService.deleteRole(..))",
        returning = "result"
    )
    public void auditRoleDeletion(JoinPoint joinPoint, Optional<Role> result) {
        String performedBy = getCurrentUsername();
        String userRole = getCurrentUserRole();
        
        result.ifPresent(role -> 
            log.warn("AUDIT [ROLE_DELETED] - ID: {}, Name: {}, Description: {}, Deleted by: {} (Role: {}), Timestamp: {}", 
                     role.getId(), 
                     role.getName(), 
                     role.getDescription(),
                     performedBy,
                     userRole,
                     LocalDateTime.now())
        );
    }

    @AfterThrowing(
        pointcut = "execution(* com.antonia.dev.userapi.service.role.RoleService.*(..))",
        throwing = "exception"
    )
    public void auditFailedRoleOperation(JoinPoint joinPoint, Exception exception) {
        String performedBy = getCurrentUsername();
        String userRole = getCurrentUserRole();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.error("AUDIT [ROLE_OPERATION_FAILED] - Method: {}, Arguments: {}, User: {} (Role: {}), Error: {}, Timestamp: {}", 
                  methodName,
                  args,
                  performedBy,
                  userRole,
                  exception.getMessage(),
                  LocalDateTime.now());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        
        return "SYSTEM";
    }

    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && authentication.getAuthorities() != null 
            && !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        
        return "UNKNOWN";
    }
}


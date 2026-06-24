package com.example.backend.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 业务自定义异常（图书不存在、预约不存在、权限操作等）
    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(RuntimeException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", e.getMessage());
        return result;
    }

    // 403 无权限访问管理员接口
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, Object> handleAccessDenied() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", "权限不足，请使用管理员账号登录");
        return result;
    }

    // 401 未登录 / token失效
    @ExceptionHandler(AuthenticationException.class)
    public Map<String, Object> handleAuthError() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", "登录已过期，请重新登录");
        return result;
    }
}

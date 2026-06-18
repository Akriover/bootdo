package com.example.usercrud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理 — 把业务异常翻译成统一 JSON 错误响应
 *
 * 状态码映射:
 *   NotFoundException      → 404
 *   DuplicateException     → 409
 *   MethodArgumentNotValid  → 400 (含字段错误)
 *   IllegalArgument        → 400
 *   其他                   → 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, Object>> duplicate(DuplicateException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage() == null ? "invalid" : f.getDefaultMessage(),
                        (a, b) -> a
                ));
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "validation failed");
        body.put("fields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegal(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unknown(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() == null ? "internal error" : e.getMessage());
    }

    private static ResponseEntity<Map<String, Object>> error(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(baseBody(status, msg));
    }

    private static Map<String, Object> baseBody(HttpStatus status, String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", msg);
        return m;
    }
}

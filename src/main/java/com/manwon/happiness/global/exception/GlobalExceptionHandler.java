package com.manwon.happiness.global.exception;

import com.manwon.happiness.member.exception.DuplicateEmailException;
import com.manwon.happiness.member.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO 유효성 검사 실패 (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // 이메일 중복 예외 (409 Conflict)
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 사용자 조회 실패 (404 Not Found)
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(MemberNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}

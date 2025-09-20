package com.manwon.happiness.auth.controller;

import com.manwon.happiness.auth.dto.LoginRequestDto;
import com.manwon.happiness.auth.dto.LoginResponseDto;
import com.manwon.happiness.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AuthController
 * - 인증/로그인 관련 API
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 로그인 API
     * POST /api/v1/auth/login
     * 성공 시 JSESSIONID 쿠키 발급
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                  HttpServletRequest httpRequest) {
        LoginResponseDto responseDto = authService.login(requestDto, httpRequest);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 로그아웃 API
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        try {
            session.invalidate();
        } catch (IllegalStateException ex) {
            // 이미 무효화된 세션인 경우 예외 발생 → 무시 처리
        }
        SecurityContextHolder.clearContext(); // 세션 초기화
        return ResponseEntity.ok("로그아웃 성공");
    }
}

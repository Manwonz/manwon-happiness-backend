package com.manwon.happiness.user.controller;

import com.manwon.happiness.user.dto.UserResponseDto;
import com.manwon.happiness.user.dto.UserSignupRequestDto;
import com.manwon.happiness.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 * - 사용자 관련 API 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 API
     * POST /api/v1/users/signup
     * 201 Created (성공)
     * 400 Bad Request (입력 오류) (@Valid 에서 자동 처리)
     * 409 Conflict (중복 이메일)
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserResponseDto responseDto = userService.registerUser(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * ID로 회원 조회 API
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * 이메일로 회원 조회 API
     * GET /api/v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }
}

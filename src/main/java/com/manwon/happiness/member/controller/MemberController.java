package com.manwon.happiness.member.controller;

import com.manwon.happiness.member.dto.MemberResponseDto;
import com.manwon.happiness.member.dto.MemberSignupRequestDto;
import com.manwon.happiness.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 * - 사용자 관련 API 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원가입 API
     * POST /api/v1/users/signup
     * 201 Created (성공)
     * 400 Bad Request (입력 오류) (@Valid 에서 자동 처리)
     * 409 Conflict (중복 이메일)
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signUp(@Valid @RequestBody MemberSignupRequestDto requestDto) {
        MemberResponseDto responseDto = memberService.registerMember(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * ID로 회원 조회 API
     * GET /api/v1/members/{member_id}
     */
    @GetMapping("/{member_id}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable("member_id") Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    /**
     * 이메일로 회원 조회 API
     * GET /api/v1/members/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberResponseDto> getMemberByEmail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.findByEmail(email));
    }
}

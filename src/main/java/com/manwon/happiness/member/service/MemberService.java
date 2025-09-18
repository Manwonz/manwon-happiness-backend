package com.manwon.happiness.member.service;

import com.manwon.happiness.member.dto.MemberResponseDto;
import com.manwon.happiness.member.dto.MemberSignupRequestDto;

public interface MemberService {
    /**
     * 회원가입
     * @param requestDto 회원가입 요청 DTO
     * @return 저장된 사용자 응답 DTO
     */
    MemberResponseDto registerMember(MemberSignupRequestDto requestDto);

    /**
     * 사용자 ID로 조회
     * @param id 사용자 ID (PK)
     * @return MemberResponseDto
     */
    MemberResponseDto findById(Long id);

    /**
     * 이메일로 사용자 찾기
     * @param email 사용자 이메일
     * @return MemberResponseDto
     */
    MemberResponseDto findByEmail(String email);
}

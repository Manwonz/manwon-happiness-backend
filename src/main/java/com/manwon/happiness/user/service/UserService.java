package com.manwon.happiness.user.service;

import com.manwon.happiness.user.dto.UserResponseDto;
import com.manwon.happiness.user.dto.UserSignupRequestDto;

public interface UserService {
    /**
     * 회원가입
     * @param requestDto 회원가입 요청 DTO
     * @return 저장된 사용자 응답 DTO
     */
    UserResponseDto registerUser(UserSignupRequestDto requestDto);

    /**
     * 사용자 ID로 조회
     * @param id 사용자 ID (PK)
     * @return UserResponseDto
     */
    UserResponseDto findById(Long id);

    /**
     * 이메일로 사용자 찾기
     * @param email 사용자 이메일
     * @return UserResponseDto
     */
    UserResponseDto findByEmail(String email);
}

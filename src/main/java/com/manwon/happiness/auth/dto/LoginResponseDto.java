package com.manwon.happiness.auth.dto;

import com.manwon.happiness.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private Long memberId;
    private String nickname;
    private Role role;
    private LocalDateTime lastLoginAt;
}

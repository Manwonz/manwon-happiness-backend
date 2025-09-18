package com.manwon.happiness.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String nickname;
}

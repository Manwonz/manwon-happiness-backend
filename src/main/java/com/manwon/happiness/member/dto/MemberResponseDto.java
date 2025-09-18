package com.manwon.happiness.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {

    @JsonProperty("member_id")
    private Long memberId;
    private String email;
    private String nickname;
}

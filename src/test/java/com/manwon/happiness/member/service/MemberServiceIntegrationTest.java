package com.manwon.happiness.member.service;

import com.manwon.happiness.member.dto.MemberResponseDto;
import com.manwon.happiness.member.dto.MemberSignupRequestDto;
import com.manwon.happiness.member.exception.DuplicateEmailException;
import com.manwon.happiness.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @DisplayName("회원가입 성공 - 이메일 중복되지 않으면 저장")
    @Test
    void 회원가입_성공() {
        // given
        MemberSignupRequestDto requestDto = MemberSignupRequestDto.builder()
                .email("test@test.com")
                .password("abcd1234")
                .nickname("tester")
                .build();

        // when
        MemberResponseDto response = memberService.registerMember(requestDto);

        // then
        assertThat(response.getMemberId()).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getNickname()).isEqualTo("tester");
    }

    @DisplayName("회원가입 실패 - 이메일 중복 시 예외 발생")
    @Test
    void 회원가입_이메일중복_예외() {
        // given
        MemberSignupRequestDto requestDto = MemberSignupRequestDto.builder()
                .email("duplicate@test.com")
                .password("password123")
                .nickname("duplicate")
                .build();

        memberService.registerMember(requestDto);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(requestDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다");
    }

    @DisplayName("회원 조회 성공 - ID로 조회")
    @Test
    void ID로_회원조회_성공() {
        // given
        MemberSignupRequestDto requestDto = MemberSignupRequestDto.builder()
                .email("findId@test.com")
                .password("password1234")
                .nickname("idMember")
                .build();

        MemberResponseDto saved = memberService.registerMember(requestDto);

        // when
        MemberResponseDto found = memberService.findById(saved.getMemberId());

        // then
        assertThat(found.getEmail()).isEqualTo("findId@test.com");
    }

    @DisplayName("회원 조회 실패 - 존재하지 않는 ID면 예외 발생")
    @Test
    void ID로_회원조회_실패() {
        // given

        // when & then
        assertThatThrownBy(() -> memberService.findById(999L))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("해당 ID를 가진 사용자가 존재하지 않습니다");
    }

    @DisplayName("회원 조회 성공 - 이메일로 조회")
    @Test
    void 이메일로_회원조회_성공() {
        // given
        MemberSignupRequestDto request = MemberSignupRequestDto.builder()
                .email("email@test.com")
                .password("password123")
                .nickname("email123")
                .build();

        memberService.registerMember(request);

        // when
        MemberResponseDto found = memberService.findByEmail("email@test.com");

        // then
        assertThat(found.getEmail()).isEqualTo("email@test.com");
        assertThat(found.getNickname()).isEqualTo("email123");
    }

    @DisplayName("회원 조회 실패 - 존재하지 않는 이메일이면 예외 발생")
    @Test
    void 이메일로_회원조회_실패() {
        // when & then
        assertThatThrownBy(() -> memberService.findByEmail("none@test.com"))
                .isInstanceOf(MemberNotFoundException.class);
    }
}

package com.manwon.happiness.member.service;

import com.manwon.happiness.member.dto.MemberResponseDto;
import com.manwon.happiness.member.dto.MemberSignupRequestDto;
import com.manwon.happiness.member.entity.Member;
import com.manwon.happiness.member.entity.Role;
import com.manwon.happiness.member.exception.DuplicateEmailException;
import com.manwon.happiness.member.exception.MemberNotFoundException;
import com.manwon.happiness.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberServiceImplUnitTest {

    private MemberRepository memberRepository;
    private MemberServiceImpl memberService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        memberService = new MemberServiceImpl(memberRepository, passwordEncoder);
    }

    @DisplayName("회원가입 성공 - 이메일 중복되지 않으면 이메일, 닉네임이 정상 저장")
    @Test
    void 회원가입_성공() {
        // given
        MemberSignupRequestDto requestDto = MemberSignupRequestDto.builder()
                .email("test@test.com")
                .password("password123")
                .nickname("testUser")
                .build();

        Member saved = Member.builder()
                .memberId(1L)
                .email(requestDto.getEmail())
                .passwordHash(requestDto.getPassword())
                .nickname(requestDto.getNickname())
                .role(Role.MEMBER)
                .build();

        when(memberRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(saved);

        // when
        MemberResponseDto responseDto = memberService.registerMember(requestDto);

        // then
        assertThat(responseDto.getEmail()).isEqualTo("test@test.com");
        assertThat(responseDto.getNickname()).isEqualTo("testUser");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("회원가입 실패 - 이메일이 중복되면 예외 발생")
    @Test
    void 회원가입_이메일중복_예외() {
        // given
        MemberSignupRequestDto requestDto = MemberSignupRequestDto.builder()
                .email("duplicate@test.com")
                .password("password123")
                .nickname("duplicateUser")
                .build();

        Member existingMember = Member.builder()
                .memberId(2L)
                .email(requestDto.getEmail())
                .passwordHash(requestDto.getPassword())
                .nickname(requestDto.getNickname())
                .role(Role.MEMBER)
                .build();

        when(memberRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(existingMember));

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(requestDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("이미 사용 중인 이메일");
    }

    @DisplayName("ID로 회원 조회 성공")
    @Test
    void ID로_회원조회_성공() {
        // given
        Member member = Member.builder()
                .memberId(1L)
                .email("find@test.com")
                .passwordHash("password1234")
                .nickname("finder")
                .role(Role.MEMBER)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        MemberResponseDto responseDto = memberService.findById(1L);

        // then
        assertThat(responseDto.getEmail()).isEqualTo("find@test.com");
        assertThat(responseDto.getNickname()).isEqualTo("finder");
    }

    @DisplayName("ID로 회원 조회 실패 - 존재하지 않으면 예외 발생")
    @Test
    void ID로_회원조회_실패() {
        // given
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findById(99L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("이메일로 회원 조회 성공")
    @Test
    void 이메일로_회원조회_성공() {
        // given
        Member member = Member.builder()
                .memberId(1L)
                .email("test@test.com")
                .passwordHash("password1234")
                .nickname("test")
                .role(Role.MEMBER)
                .build();

        when(memberRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(member));

        // when
        MemberResponseDto responseDto = memberService.findByEmail("test@test.com");

        // then
        assertThat(responseDto.getMemberId()).isEqualTo(1L);
        assertThat(responseDto.getNickname()).isEqualTo("test");
    }

    @DisplayName("이메일로 회원 조회 실패 - 존재하지 않으면 예외 발생")
    @Test
    void 이메일로_회원조회_실패() {
        // given
        when(memberRepository.findByEmail("none@test.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findByEmail("none@test.com"))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
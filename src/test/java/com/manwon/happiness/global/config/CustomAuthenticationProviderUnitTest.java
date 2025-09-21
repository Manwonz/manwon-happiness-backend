package com.manwon.happiness.global.config;

import com.manwon.happiness.member.entity.Member;
import com.manwon.happiness.member.entity.Role;
import com.manwon.happiness.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomAuthenticationProviderUnitTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        Member member = Member.builder()
                .email("unit@test.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .nickname("unitTester")
                .role(Role.MEMBER)
                .build();
        memberRepository.save(member);
    }

    @DisplayName("커스텀 AuthenticationProvider - 로그인 성공")
    @Test
    void 로그인성공() {
        // given
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("unit@test.com", "password123");

        // when
        Authentication authentication = authenticationManager.authenticate(token);

        // then
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("unit@test.com", authentication.getName());
    }

    @DisplayName("커스텀 AuthenticationProvider - 로그인 실패 (비밀번호 불일치)")
    @Test
    void 로그인실패() {
        // given
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("unit@test.com", "wrongPassword");

        // when & then
        assertThrows(BadCredentialsException.class, () ->
                authenticationManager.authenticate(token)
        );
    }
}
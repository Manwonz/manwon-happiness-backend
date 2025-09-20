package com.manwon.happiness.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manwon.happiness.auth.dto.LoginRequestDto;
import com.manwon.happiness.member.entity.Member;
import com.manwon.happiness.member.entity.Role;
import com.manwon.happiness.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        Member member = Member.builder()
                .email("test@test.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .nickname("tester")
                .role(Role.MEMBER)
                .createdAt(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }

    @DisplayName("로그인 성공 시 세션 발급")
    @Test
    void 로그인_성공() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("test@test.com", "password123");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                // 세션에 SecurityContext가 들어갔는지 확인
                .andExpect(request().sessionAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        notNullValue()
                ))
                .andExpect(jsonPath("$.nickname").value("tester"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @DisplayName("로그아웃 성공 시 세션 무효화")
    @Test
    void 로그아웃_성공() throws Exception {
        // 1. 먼저 로그인해서 세션 얻기
        LoginRequestDto requestDto = new LoginRequestDto("test@test.com", "password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        // 2. 로그인된 세션으로 로그아웃 요청
        mockMvc.perform(post("/api/v1/auth/logout").with(csrf()).session(session))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 조회 - 로그인 필요")
    @Test
    void 로그인_회원조회() throws Exception {
        // given
        Long anyId = 1L;

        mockMvc.perform(get("/api/v1/members/{member_id}", anyId))
                .andExpect(status().isForbidden()); // 로그인 안했으니까 403

    }
}
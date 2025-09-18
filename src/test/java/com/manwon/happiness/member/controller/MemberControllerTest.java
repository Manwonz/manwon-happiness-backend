package com.manwon.happiness.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manwon.happiness.member.dto.MemberSignupRequestDto;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공 - 올바른 요청이면 201 Created")
    void 회원가입_성공() throws Exception {
        // given
        MemberSignupRequestDto requestDto = new MemberSignupRequestDto();
        requestDto.setEmail("test1@test.com");
        requestDto.setPassword("password123");
        requestDto.setNickname("testUser1");

        // when & then
        mockMvc.perform(post("/api/v1/members/signup") // 실제 Controller 매핑 URL 맞춰줘야 함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()) // HTTP 201 응답
                .andExpect(jsonPath("$.email").value("test1@test.com"))
                .andExpect(jsonPath("$.nickname").value("testUser1"));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일 409 Conflict")
    void 회원가입_실패_중복이메일() throws Exception {
        // given
        Member existing = Member.builder()
                .email("duplicate@test.com")
                .passwordHash("password123")
                .nickname("duplicateUser")
                .role(Role.MEMBER)
                .createdAt(LocalDateTime.now())
                .build();
        memberRepository.save(existing);

        MemberSignupRequestDto requestDto = new MemberSignupRequestDto();
        requestDto.setEmail("duplicate@test.com");
        requestDto.setPassword("password456");
        requestDto.setNickname("newUser");

        // when & then
        mockMvc.perform(post("/api/v1/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검증 실패 400 Bad Request")
    void 회원가입_실패_유효성검증() throws Exception {
        // given (잘못된 입력값)
        MemberSignupRequestDto requestDto = new MemberSignupRequestDto();
        requestDto.setEmail("not-an-email"); // 잘못된 이메일 형식
        requestDto.setPassword("123"); // 너무 짧은 비밀번호
        requestDto.setNickname(""); // 빈 닉네임

        // when & then
        mockMvc.perform(post("/api/v1/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("회원 ID로 조회 성공 - 200 OK")
    void ID로_회원조회_성공() throws Exception {
        // given
        Member member = Member.builder()
                .email("findid@test.com")
                .passwordHash("password123")
                .nickname("findByIdUser")
                .role(Role.MEMBER)
                .createdAt(LocalDateTime.now())
                .build();
        Member saved = memberRepository.save(member);

        // when & then
        mockMvc.perform(get("/api/v1/members/{member_id}", saved.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("findid@test.com"))
                .andExpect(jsonPath("$.nickname").value("findByIdUser"));
    }

    @DisplayName("회원 조회 실패 - 없는 ID 404 Not Found")
    @Test
    void 회원조회_실패_ID() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/members/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 이메일로 조회 성공 - 200 OK")
    void 이메일로_회원조회_성공() throws Exception {
        // given
        Member member = Member.builder()
                .email("findemail@test.com")
                .passwordHash("password123")
                .nickname("findByEmailUser")
                .role(Role.MEMBER)
                .createdAt(LocalDateTime.now())
                .build();
        memberRepository.save(member);

        // when & then
        mockMvc.perform(get("/api/v1/members/email/{email}", "findemail@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("findemail@test.com"))
                .andExpect(jsonPath("$.nickname").value("findByEmailUser"));
    }

    @Test
    @DisplayName("회원 조회 실패 - 없는 이메일 404 Not Found")
    void 회원조회_실패_이메일() throws Exception {
        // given
        String notExistEmail = "notfound@test.com";

        // when & then
        mockMvc.perform(get("/api/v1/members/email/{email}", notExistEmail))
                .andExpect(status().isNotFound()); // 404
    }
}
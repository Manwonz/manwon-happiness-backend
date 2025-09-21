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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

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

//    @DisplayName("로그인 성공 시 세션 발급")
//    @Test
//    void 로그인_성공() throws Exception {
//        // given
//        LoginRequestDto requestDto = new LoginRequestDto("test@test.com", "password123");
//
//        // when & then
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                // 세션에 SecurityContext가 들어갔는지 확인
//                .andExpect(request().sessionAttribute(
//                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//                        notNullValue()
//                ))
//                .andExpect(jsonPath("$.nickname").value("tester"))
//                .andExpect(jsonPath("$.role").value("MEMBER"));
//    }
@DisplayName("로그인 성공 시 세션 + SecurityContext 저장/인증 정보 확인")
@Test
void 로그인_성공() throws Exception {
    // given
    LoginRequestDto requestDto = new LoginRequestDto("test@test.com", "password123");

    // when
    MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            // 응답 바디 확인
            .andExpect(jsonPath("$.nickname").value("tester"))
            .andExpect(jsonPath("$.role").value("MEMBER"))
            .andReturn();

    // then — 세션/시큐리티 컨텍스트/인증 객체 검증
    MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
    assertNotNull(session, "세션이 생성되어야 합니다.");

    Object ctxAttr = session.getAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
    );
    assertNotNull(ctxAttr, "SecurityContext가 세션에 저장되어야 합니다.");

    SecurityContext context = (SecurityContext) ctxAttr;
    Authentication auth = context.getAuthentication();
    assertNotNull(auth, "Authentication이 존재해야 합니다.");
    assertTrue(auth.isAuthenticated(), "인증 상태여야 합니다.");

    // 인증 성공 후 credentials는 null이어야 함
    assertNull(auth.getCredentials(), "인증 성공 후 credentials는 null이어야 합니다.");

    // 사용자명(email) 확인
    assertEquals("test@test.com", auth.getName());

    // principal 타입/값 확인
    Object principal = auth.getPrincipal();
    assertTrue(principal instanceof com.manwon.happiness.auth.model.CustomUserDetails);
    com.manwon.happiness.auth.model.CustomUserDetails user =
            (com.manwon.happiness.auth.model.CustomUserDetails) principal;

    assertEquals("tester", user.getNickname());

    // 권한 확인 (ROLE_MEMBER)
    assertTrue(
            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER")),
            "ROLE_MEMBER 권한이어야 합니다."
    );

    // (선택) 세션 타임아웃 검증 — AuthService에서 MEMBER 30분으로 설정
    assertEquals(30 * 60, session.getMaxInactiveInterval(), "세션 타임아웃(초) 불일치");
}

    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    @Test
    void 로그인실패_비밀번호틀림() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("test@test.com", "wrongpassword");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    @Test
    void 로그인실패_이메일없음() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("notexist@test.com", "password123");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
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
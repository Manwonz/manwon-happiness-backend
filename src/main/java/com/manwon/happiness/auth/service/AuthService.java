package com.manwon.happiness.auth.service;

import com.manwon.happiness.auth.dto.LoginRequestDto;
import com.manwon.happiness.auth.dto.LoginResponseDto;
import com.manwon.happiness.member.entity.Member;
import com.manwon.happiness.member.entity.Role;
import com.manwon.happiness.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;

    public AuthService(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.memberRepository = memberRepository;
    }

    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletRequest httpRequest) {
        // 1. 로그인 시도 - 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        // 2, AuthenticationManager에게 인증 위임
        Authentication authentication = authenticationManager.authenticate(token);

        // 3. 인증 정보 세션(SecurityContext)에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 세션에 SecurityContext 저장 (세션 기반 로그인 유지)
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다"));

        // 유저마다 다른 세션 만료 정책 설정
        if (member.getRole() == Role.ADMIN) {
            session.setMaxInactiveInterval(60 * 60); // 관리자 : 1시간
        } else {
            session.setMaxInactiveInterval(30 * 60); // 일반회원 : 30분
        }

        // 5. 마지막 로그인 시간 업데이트
        member.updateLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

        return new LoginResponseDto(
                member.getMemberId(),
                member.getNickname(),
                member.getRole(),
                member.getLastLoginAt()
        );
    }
}

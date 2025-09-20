package com.manwon.happiness.member.service;

import com.manwon.happiness.member.dto.MemberResponseDto;
import com.manwon.happiness.member.dto.MemberSignupRequestDto;
import com.manwon.happiness.member.entity.Member;
import com.manwon.happiness.member.entity.Role;
import com.manwon.happiness.member.exception.DuplicateEmailException;
import com.manwon.happiness.member.exception.DuplicateNicknameException;
import com.manwon.happiness.member.exception.MemberNotFoundException;
import com.manwon.happiness.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 세션 로그인 추가

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입
     */
    @Override
    public MemberResponseDto registerMember(MemberSignupRequestDto requestDto) {
        // 이메일 중복 체크
        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + requestDto.getEmail());
        }

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다: " + requestDto.getNickname());
        }

        // DTO -> 엔티티 변환
        Member member = requestDto.toEntity(passwordEncoder);

        // DB 저장
        Member savedMember = memberRepository.save(member);

        // 엔티티 -> DTO 변환 후 반환
        return new MemberResponseDto(savedMember.getMemberId(), savedMember.getEmail(), savedMember.getNickname());
    }

    /**
     * ID로 사용자 찾기
     */
    @Override
    public MemberResponseDto findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("해당 ID를 가진 사용자가 존재하지 않습니다. : " + id));

        return new MemberResponseDto(member.getMemberId(), member.getEmail(), member.getNickname());
    }

    /**
     * 이메일로 사용자 찾기
     */
    @Override
    public MemberResponseDto findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("해당 Email을 가진 사용자가 존재하지 않습니다. : " + email));

        return new MemberResponseDto(member.getMemberId(), member.getEmail(), member.getNickname());
    }
}

package com.manwon.happiness.user.service;

import com.manwon.happiness.user.dto.UserResponseDto;
import com.manwon.happiness.user.dto.UserSignupRequestDto;
import com.manwon.happiness.user.entity.Role;
import com.manwon.happiness.user.entity.User;
import com.manwon.happiness.user.exception.DuplicateEmailException;
import com.manwon.happiness.user.exception.UserNotFoundException;
import com.manwon.happiness.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원가입
     */
    @Override
    public UserResponseDto registerUser(UserSignupRequestDto requestDto) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + requestDto.getEmail());
        }

        // DTO -> 엔티티 변환 (Builder 사용)
        User user = User.builder()
                .email(requestDto.getEmail())
                .passwordHash(requestDto.getPassword()) // TODO: 비밀번호 암호화 필요
                .nickname(requestDto.getNickname())
                .role(Role.USER)
                .build();

        // DB 저장
        User savedUser = userRepository.save(user);

        // 엔티티 -> DTO 변환 후 반환
        return new UserResponseDto(savedUser.getUserId(), savedUser.getEmail(), savedUser.getNickname());
    }

    /**
     * ID로 사용자 찾기
     */
    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("해당 ID를 가진 사용자가 존재하지 않습니다. : " + id));

        return new UserResponseDto(user.getUserId(), user.getEmail(), user.getNickname());
    }

    /**
     * 이메일로 사용자 찾기
     */
    @Override
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 Email을 가진 사용자가 존재하지 않습니다. : " + email));

        return new UserResponseDto(user.getUserId(), user.getEmail(), user.getNickname());
    }
}

package com.manwon.happiness.member.exception;

/**
 * 닉네임 중복 시 발생하는 예외
 */
public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String message) {
        super(message);
    }
}

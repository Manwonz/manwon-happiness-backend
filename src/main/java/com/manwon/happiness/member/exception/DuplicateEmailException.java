package com.manwon.happiness.member.exception;

/**
 * 이메일 중복 시 발생하는 예외
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}

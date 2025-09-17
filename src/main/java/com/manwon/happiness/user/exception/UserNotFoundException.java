package com.manwon.happiness.user.exception;

/**
 * 사용자 조회 실패 시 발생하는 예외
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

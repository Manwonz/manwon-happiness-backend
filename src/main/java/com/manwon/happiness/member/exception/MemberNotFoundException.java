package com.manwon.happiness.member.exception;

/**
 * 사용자 조회 실패 시 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}

package com.manwon.happiness.member.repository;

import com.manwon.happiness.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 찾기
    Optional<Member> findByEmail(String email);
}

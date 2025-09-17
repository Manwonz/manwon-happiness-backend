package com.manwon.happiness.user.repository;

import com.manwon.happiness.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 찾기
    Optional<User> findByEmail(String email);
}

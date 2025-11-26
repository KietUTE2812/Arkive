package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.ForgotPasswordToken;
import com.example.arkivebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, String> {
    Optional<ForgotPasswordToken> findByToken(String token);
    void deleteByUser(User user);
}

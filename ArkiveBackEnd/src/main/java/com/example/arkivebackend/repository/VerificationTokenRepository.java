package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}

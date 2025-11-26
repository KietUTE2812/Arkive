package com.example.arkivebackend.util;

import com.example.arkivebackend.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailUtil {

    VerificationTokenRepository verificationTokenRepository;

    public String generateRandomCode() {
        // Generate a random 6-digit verification code
        int code = (int)(Math.random() * 900000) + 100000;
        if (verificationTokenRepository.findByToken(String.valueOf(code)).isPresent()) {
            return generateRandomCode(); // Regenerate if code already exists
        }
        return String.valueOf(code);
    }

}

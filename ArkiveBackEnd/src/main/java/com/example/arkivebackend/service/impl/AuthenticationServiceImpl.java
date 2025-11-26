package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.AuthenticationRequest;
import com.example.arkivebackend.dto.request.IntrospectRequest;
import com.example.arkivebackend.dto.request.RegisterRequest;
import com.example.arkivebackend.dto.response.AuthenticationResponse;
import com.example.arkivebackend.dto.response.IntrospectResponse;
import com.example.arkivebackend.dto.response.UserResponse;
import com.example.arkivebackend.entity.ForgotPasswordToken;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.entity.VerificationToken;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.*;
import com.example.arkivebackend.service.AuthenticationService;
import com.example.arkivebackend.service.EmailService;
import com.example.arkivebackend.util.EmailUtil;
import com.example.arkivebackend.util.TokenUtil;
import com.nimbusds.jose.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)

public class AuthenticationServiceImpl implements AuthenticationService {

    @NonFinal
    @Value("${front-end.url}")
    String FRONT_END_URL;

    @NonFinal
    @Value("${app.name}:Arkive")
    String APP_NAME;

    InvalidatedTokenRepository invalidatedTokenRepository;
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    EmailService emailService;
    VerificationTokenRepository verificationTokenRepository;
    EmailUtil emailUtil;
    ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    TokenUtil tokenUtil;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest) { // Xác thực người dùng (Đăng nhập)
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.isVerified()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }
        // Password verification logic should be here (e.g., using BCrypt)
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        // Generate token logic should be here (e.g., JWT)
        String token = tokenUtil.generateToken(user, false);
        String refreshToken = tokenUtil.generateToken(user, true);
        tokenUtil.saveRefreshToken(refreshToken, user, false, httpServletRequest.getHeader("User-Agent"), getClientIp(httpServletRequest));
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Kiểm tra tính hợp lệ của dữ liệu (username, email)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        try {
            // 2. Tạo người dùng mới và lưu vào cơ sở dữ liệu
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User newUser = User.builder()
                    .username(request.getUsername())
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(newUser);
            // String token = generateToken(newUser, false);
            // String refreshToken = generateToken(newUser, true);
            // saveRefreshToken(refreshToken, newUser, false, "N/A", "N/A");

            // 3. Tạo token và gửi mail xác thực
            String veryficationToken = emailUtil.generateRandomCode();
            // Lưu token xác thực vào cơ sở dữ liệu (cần tạo entity và repository cho VerificationToken)
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(veryficationToken);
            verificationToken.setUser(newUser);
            verificationToken.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES)); // Token hết hạn sau 10 phút
            verificationTokenRepository.save(verificationToken);
            // Gửi email xác thực
            String subject = "[{APP_NAME}] Xác thực tài khoản của bạn".replace("{APP_NAME}", APP_NAME);
            String htmlBody = readHtmlTemplate("emails/verification-email.html")
                    .replace("{verification_code}", veryficationToken);
            emailService.sendAttachmentEmail(newUser.getEmail(), subject, htmlBody);
            return UserResponse.builder()
                    .id(newUser.getId())
                    .username(newUser.getUsername())
                    .fullName(newUser.getFullName())
                    .email(newUser.getEmail())
                    .isVerified(false)
                    .build();
        }
        catch (IOException e) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public boolean verifyEmail(String verificationCode) {
        log.info("Verifying email with code: {}", verificationCode);
        VerificationToken verificationToken = verificationTokenRepository.findByToken(verificationCode)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_TOKEN_INVALID));
        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        }
        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken); // Xoá token sau khi xác thực thành công
        return true;
    }

    @Transactional
    @Override
    public boolean resendVerificationCode(String email) throws IOException, MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.isVerified()) {
            throw new AppException(ErrorCode.ACCOUNT_ALREADY_ACTIVATED);
        }

        // Xoá các token xác thực cũ (nếu có)
        verificationTokenRepository.deleteByUser(user);

        // Tạo và lưu token xác thực mới
        String newVerificationToken = emailUtil.generateRandomCode();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(newVerificationToken);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES)); // Token hết hạn sau 10 phút
        verificationTokenRepository.save(verificationToken);
        // Gửi email xác thực mới
        String subject = "[{APP_NAME}] Mã xác thực tài khoản của bạn".replace("{APP_NAME}", APP_NAME);
        String htmlBody = readHtmlTemplate("verification-email.html")
                .replace("{verification_code}", newVerificationToken);
        emailService.sendAttachmentEmail(user.getEmail(), subject, htmlBody);
        return true;
    }

    @Override
    public boolean logout(String refreshToken) throws ParseException, JOSEException {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            var storedRefreshToken = refreshTokenRepository.findByToken(refreshToken);
            storedRefreshToken.ifPresent(rt -> {
                rt.setIsRevoked(true);
                refreshTokenRepository.save(rt);
            });
        }
        return true;
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken, HttpServletRequest httpServletRequest) throws ParseException, JOSEException {
        // Verify the refresh token (UUID)
        var storedRefreshToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedRefreshToken.isEmpty() || storedRefreshToken.get().getIsRevoked() || storedRefreshToken.get().getExpirationDate().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        User user = storedRefreshToken.get().getUser();

        // Revoke the old refresh token
        storedRefreshToken.get().setIsRevoked(true);
        refreshTokenRepository.save(storedRefreshToken.get());

        // Generate new tokens
        String newToken = tokenUtil.generateToken(user, false);
        String newRefreshToken = tokenUtil.generateToken(user, true);
        tokenUtil.saveRefreshToken(newRefreshToken, user, false, httpServletRequest.getHeader("User-Agent"), getClientIp(httpServletRequest));
        
        return AuthenticationResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .authenticated(true)
                .build();
    }

    @Transactional
    @Override
    public Boolean forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Tạo mã đặt lại mật khẩu
        String resetToken = emailUtil.generateRandomCode();
        // Lưu mã đặt lại mật khẩu vào cơ sở dữ liệu
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setToken(resetToken);
        forgotPasswordToken.setUser(user);
        forgotPasswordToken.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES));
        forgotPasswordTokenRepository.save(forgotPasswordToken);

        String subject = "[{APP_NAME}] Đặt lại mật khẩu của bạn".replace("{APP_NAME}", APP_NAME);
        String htmlBody;
        try {
            htmlBody = readHtmlTemplate("passwords/forgot-password-email.html")
                    .replace("{user_name}", user.getFullName())
                    .replace("{reset_link}", FRONT_END_URL + "/reset-password?token=" + resetToken);
            emailService.sendAttachmentEmail(user.getEmail(), subject, htmlBody);
        } catch (IOException | MessagingException e) {
            log.error("Failed to send forgot password email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean resetPassword(String token, String newPassword) {
        ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.RESET_PASSWORD_TOKEN_INVALID));
        if (forgotPasswordToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.RESET_PASSWORD_TOKEN_EXPIRED);
        }
        User user = forgotPasswordToken.getUser();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Xoá token sau khi đặt lại mật khẩu thành công
        forgotPasswordTokenRepository.delete(forgotPasswordToken);
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String readHtmlTemplate(String templateName) throws IOException {
        var resource = new org.springframework.core.io.ClassPathResource("templates/" + templateName);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        // // Check token validity
        // var isLoggedOut = invalidatedTokenRepository.existsById(
        //         SignedJWT.parse(token).getJWTClaimsSet().getJWTID());

        // if (isLoggedOut) {
        //     return IntrospectResponse.builder().valid(false).build();
        // }

         // Verify token signature and expiration
        try {
            tokenUtil.verifyToken(token, false);
        } catch (AppException e) {
            return IntrospectResponse.builder().valid(false).build();
        }
        return IntrospectResponse.builder().valid(true).build();
    }
}

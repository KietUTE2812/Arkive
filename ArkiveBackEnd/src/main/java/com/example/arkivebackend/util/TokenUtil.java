package com.example.arkivebackend.util;

import com.example.arkivebackend.entity.RefreshToken;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.InvalidatedTokenRepository;
import com.example.arkivebackend.repository.RefreshTokenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenUtil {

    @Value("${jwt.secretkey}")
    String secretkey;

    @Value("${jwt.valid-duration}")
    Long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    Long REFRESHABLE_DURATION;

    RefreshTokenRepository refreshTokenRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public TokenUtil(RefreshTokenRepository refreshTokenRepository,
                     InvalidatedTokenRepository invalidatedTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    public String generateToken(User user, boolean isRefresh) {

        if (isRefresh) {
            UUID refreshTokenId = UUID.randomUUID();
            return refreshTokenId.toString();
        }

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("codeverest.io.vn")
                .claim("userId", user.getId())
                .claim("scope", buildScope(user))
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(Date.from(Instant.now().plus(VALID_DURATION, ChronoUnit.MILLIS))) // Set expiration time in milliseconds
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretkey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("JWT generation failed: ", e);
            throw new RuntimeException(e);
        }
    }

    public SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secretkey);
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Date expirationDate = (isRefresh)
                ? Date.from(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.MILLIS))
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationDate.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    public void saveRefreshToken(String token, User user, Boolean isRevoked, String userAgent, String ipAddress) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(token)
                        .user(user)
                        .expirationDate(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.MILLIS))
                        .isRevoked(isRevoked)
                        .userAgent(userAgent)
                        .createdByIp(ipAddress)
                        .build());
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }
}

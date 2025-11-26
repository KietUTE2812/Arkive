package com.example.arkivebackend.config;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.secretkey}")
    private String secretKey;

    // Hàm giải mã và xác thực JWT
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Validate token structure
            if (signedJWT.getJWTClaimsSet() == null) {
                throw new JwtException("Token claims are null");
            }

            // Check if token is expired
            if (signedJWT.getJWTClaimsSet().getExpirationTime() != null &&
                signedJWT.getJWTClaimsSet().getExpirationTime().before(new java.util.Date())) {
                throw new JwtException("Token is expired");
            }

            return new Jwt(token,
                    signedJWT.getJWTClaimsSet().getIssueTime() != null ?
                        signedJWT.getJWTClaimsSet().getIssueTime().toInstant() : java.time.Instant.now(),
                    signedJWT.getJWTClaimsSet().getExpirationTime() != null ?
                        signedJWT.getJWTClaimsSet().getExpirationTime().toInstant() : java.time.Instant.now().plusSeconds(3600),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims()
                    );
        } catch (ParseException e) {
            throw new JwtException("Invalid Token: " + e.getMessage());
        } catch (Exception e) {
            throw new JwtException("Token processing error: " + e.getMessage());
        }
    }
}

package com.ecommerce.ProductService.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Arrays;

@Slf4j
@Configuration
public class JwtTokenProvider {
    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        log.debug(Arrays.toString(secret.getBytes()));

        this.expirationMs = expirationMs;
        log.debug(String.valueOf(this.expirationMs));
    }
    public Claims validateToken(String token) {
        log.debug(String.valueOf(this.key));
        log.debug(String.valueOf(this.expirationMs));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

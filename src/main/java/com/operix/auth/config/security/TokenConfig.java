package com.operix.auth.config.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.operix.auth.entity.User;
import com.operix.auth.entity.Superuser;

@Component
public class TokenConfig {

    private static final String secret = UUID.randomUUID().toString();
    private static final long expiration = 86400;
    private static final long suExpiration = 30;

    Algorithm algorithm = Algorithm.HMAC256(secret);

    public String generateToken(User user) {
        return JWT.create()
                .withClaim("userId", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(expiration))
                .sign(algorithm);
    }

    public String generateToken(Superuser superuser) {
        return JWT.create()
                .withClaim("userId", superuser.getId())
                .withSubject(superuser.getEmail())
                .withExpiresAt(Instant.now().plus(suExpiration, ChronoUnit.DAYS))
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm).build().verify(token);
            return Optional.of(JWTUserData.builder()
                    .userId(decode.getClaim("userId").asLong())
                    .email(decode.getSubject())
                    .build());
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }

    }

}

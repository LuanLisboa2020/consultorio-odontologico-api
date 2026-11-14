
package com.consultorio.consultorio_odontologico_api.security;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${api.security.jwt.secret}") String secret,
            @Value("${api.security.jwt.access-token-expiration}") long expiration) {

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        this.accessTokenExpiration = expiration;
    }

    public String gerarAccessToken(UUID usuarioId) {

        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(usuarioId.toString())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(accessTokenExpiration)))
                .signWith(signingKey)
                .compact();
    }

    public UUID extrairUsuarioId(String token) {

        String usuarioId = extrairClaims(token).getSubject();

        return UUID.fromString(usuarioId);
    }

    public boolean validarToken(String token) {

        try {
            extrairClaims(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

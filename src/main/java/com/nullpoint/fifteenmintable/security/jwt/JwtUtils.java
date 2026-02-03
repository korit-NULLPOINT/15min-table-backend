package com.nullpoint.fifteenmintable.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey KEY;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .subject("AccessToken")
                .id(userId)
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 15L)))
                .signWith(KEY)
                .compact();
    }

    public String generateVerifyToken(String userId) {
        return Jwts.builder()
                .subject("VerifyToken")
                .id(userId)
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 3L)))
                .signWith(KEY)
                .compact();
    }

    public Claims getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isBearer(String token) {
        if (token == null) {
            return false;
        }

        return token.startsWith("Bearer ");
    }

    public String removeBearer(String token) {
        return token.replaceFirst("Bearer ", "");
    }
}

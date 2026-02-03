package com.nullpoint.fifteenmintable.security.auth.token;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final String pepper;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(@Value("${security.refresh.pepper}") String pepper) {
        if (pepper == null || pepper.isBlank()) {
            throw new IllegalStateException("security.refresh.pepper is required");
        }
        this.pepper = pepper;
    }

    /** refresh token = sessionId.secret */
    public String issueRefreshToken(String sessionId) {
        byte[] buf = new byte[32];
        random.nextBytes(buf);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
        return sessionId + "." + secret;
    }

    /** DB/Redis에는 secret 원문이 아니라 hash를 저장 */
    public String hashSecret(String secret) {
        if (secret == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((secret + ":" + pepper).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("refresh secret hash failed", e);
        }
    }

    public String extractSessionId(String refreshToken) {
        if (refreshToken == null) return null;
        int idx = refreshToken.indexOf('.');
        if (idx <= 0) return null;
        return refreshToken.substring(0, idx);
    }

    public String extractSecret(String refreshToken) {
        if (refreshToken == null) return null;
        int idx = refreshToken.indexOf('.');
        if (idx <= 0 || idx == refreshToken.length() - 1) return null;
        return refreshToken.substring(idx + 1);
    }
}

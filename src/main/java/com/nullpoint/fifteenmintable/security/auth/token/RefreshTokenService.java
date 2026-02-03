package com.nullpoint.fifteenmintable.security.auth.token;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class RefreshTokenService {

    // 운영에선 환경변수로 빼는 걸 추천. (지금은 간단히 하드코딩)
    private static final String PEPPER = "CHANGE_ME_PEPPER";

    private final SecureRandom random = new SecureRandom();

    /** refresh token = sessionId.secret */
    public String issueRefreshToken(String sessionId) {
        byte[] buf = new byte[32];
        random.nextBytes(buf);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
        return sessionId + "." + secret;
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

    /** DB/Redis에는 secret 원문이 아니라 hash를 저장 */
    public String hashSecret(String secret) {
        if (secret == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((secret + ":" + PEPPER).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("refresh secret hash failed", e);
        }
    }
}

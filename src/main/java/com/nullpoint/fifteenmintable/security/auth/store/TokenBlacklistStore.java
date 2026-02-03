package com.nullpoint.fifteenmintable.security.auth.store;

import org.springframework.stereotype.Component;

@Component
public interface TokenBlacklistStore {
    void blacklist(String token, long ttlSeconds);
    boolean isBlacklisted(String token);
}

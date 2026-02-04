package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.AuthSession;
import com.nullpoint.fifteenmintable.mapper.AuthSessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AuthSessionRepository {

    @Autowired
    private AuthSessionMapper authSessionMapper;

    public int insertSession(AuthSession session) {
        return authSessionMapper.insertSession(session);
    }

    public Optional<AuthSession> getBySessionId(String sessionId) {
        return authSessionMapper.getBySessionId(sessionId);
    }

    public int rotateSession(
            String sessionId,
            String refreshTokenHash,
            LocalDateTime expiresDt,
            LocalDateTime lastUsedDt,
            LocalDateTime updateDt
    ) {
        return authSessionMapper.rotateSession(sessionId, refreshTokenHash, expiresDt, lastUsedDt, updateDt);
    }
}

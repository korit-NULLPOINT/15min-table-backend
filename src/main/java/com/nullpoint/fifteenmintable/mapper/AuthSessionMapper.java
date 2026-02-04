package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.AuthSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
public interface AuthSessionMapper {

    int insertSession(AuthSession session);

    Optional<AuthSession> getBySessionId(@Param("sessionId") String sessionId);

    int rotateSession(
            @Param("sessionId") String sessionId,
            @Param("refreshTokenHash") String refreshTokenHash,
            @Param("expiresDt") LocalDateTime expiresDt,
            @Param("lastUsedDt") LocalDateTime lastUsedDt,
            @Param("updateDt") LocalDateTime updateDt
    );

    int revokeSession(
            @Param("sessionId") String sessionId,
            @Param("revokedDt") LocalDateTime revokedDt,
            @Param("updateDt") LocalDateTime updateDt
    );
}

package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthSession {
    private Integer authSessionId;
    private String sessionId;
    private Integer userId;
    private String refreshTokenHash;
    private String status; // ACTIVE | REVOKED | EXPIRED
    private String ip;
    private String userAgent;
    private LocalDateTime lastUsedDt;
    private LocalDateTime expiresDt;
    private LocalDateTime revokedDt;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}

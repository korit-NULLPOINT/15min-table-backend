package com.nullpoint.fifteenmintable.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nullpoint.fifteenmintable.entity.UserRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String email;
    @JsonIgnore
    private String password;
    private String username;
    private String profileImgUrl;
    private String status;
    private List<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRoles == null) return List.of();

        return userRoles.stream()
                .map(UserRole::getRole)
                .filter(role -> role != null && role.getRoleName() != null)
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    // =======================
    // Helpers
    // =======================

    @JsonIgnore
    public boolean hasAuthority(String roleName) {
        if (roleName == null || roleName.isBlank()) return false;

        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleName::equals);
    }

    @JsonIgnore
    public boolean isTempUser() {
        return hasAuthority("ROLE_TEMP_USER");
    }

    @JsonIgnore
    public boolean isUser() {
        return hasAuthority("ROLE_USER");
    }

    @JsonIgnore
    public boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }

    /**
     * 인증/인가에서 보통 "일반 기능 사용 가능" 기준으로 쓰기 좋은 헬퍼
     * - TEMP는 false
     * - USER/ADMIN은 true
     */
    @JsonIgnore
    public boolean isVerifiedUser() {
        return isUser() || isAdmin();
    }
}

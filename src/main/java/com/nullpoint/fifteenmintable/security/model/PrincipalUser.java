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
}

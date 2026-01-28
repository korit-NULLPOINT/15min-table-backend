package com.nullpoint.fifteenmintable.service;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PrincipalLoaderService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<PrincipalUser> loadByUserId(Integer userId) {
        return userRepository.getUserByUserId(userId).map(user ->
                PrincipalUser.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .username(user.getUsername())
                        .profileImgUrl(user.getProfileImgUrl())
                        .status(user.getStatus())
                        .userRoles(user.getUserRoles())
                        .build()
        );
    }
}

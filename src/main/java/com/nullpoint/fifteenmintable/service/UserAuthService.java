package com.nullpoint.fifteenmintable.service;


import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.auth.SignupReqDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.entity.UserRole;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserAuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public ApiRespDto<?> signup(SignupReqDto signupReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(signupReqDto.getEmail());
        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 이메일 입니다.", null);
        }

        Optional<User> foundUserByUsername = userRepository.getUserByUsername(signupReqDto.getUsername());
        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 사용자 이름 입니다.", null);
        }

        Optional<User> optionalUser = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("회원 추가에 실패했습니다.");
        }

        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();

        int result = userRoleRepository.addUserRole(userRole);
        if (result != 1) {
            throw new RuntimeException("회원 권한 추가에 실패했습니다.");
        }

        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", optionalUser.get());
    }
    }











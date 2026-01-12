package com.nullpoint.fifteenmintable.service;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.oauth2.OAuth2MergeReqDto;
import com.nullpoint.fifteenmintable.dto.oauth2.OAuth2SignupReqDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.entity.UserRole;
import com.nullpoint.fifteenmintable.repository.OAuth2UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OAuth2AuthService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Transactional(rollbackFor = Exception.class)
    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());
        if (foundUser.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 이메일 입니다.", null);
        }

        Optional<User> foundUserByUsername = userRepository.getUserByUsername(oAuth2SignupReqDto.getUsername());
        if (foundUserByUsername.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 사용자 이름 입니다.", null);
        }

        Optional<User> optionalUser = userRepository.addUser(oAuth2SignupReqDto.toUserEntity(bCryptPasswordEncoder));
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("회원 추가에 실패했습니다.");
        }

        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)
                .build();

        int userRoleResult = userRoleRepository.addUserRole(userRole);
        if (userRoleResult != 1) {
            throw new RuntimeException("회원 권한 추가에 실패했습니다.");
        }

        int oauth2UserResult = oAuth2UserRepository.addOAuth2User(oAuth2SignupReqDto.toOAuth2UserEntity(optionalUser.get().getUserId()));
        if (oauth2UserResult != 1) {
            throw new RuntimeException("OAuth2 추가에 실패했습니다.");
        }


        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", optionalUser.get());
    }

    public ApiRespDto<?> merge(OAuth2MergeReqDto oAuth2MergeReqDto) {
        Optional<User> foundUser = userRepository.getUserByEmail(oAuth2MergeReqDto.getEmail());
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보를 다시 확인해주세요.", null);
        }

        if (!bCryptPasswordEncoder.matches(oAuth2MergeReqDto.getPassword(), foundUser.get().getPassword())) {
            return new ApiRespDto<>("failed", "사용자 정보를 다시 확인해주세요.", null);
        }

        int result = oAuth2UserRepository.addOAuth2User(oAuth2MergeReqDto.toEntity(foundUser.get().getUserId()));
        if (result != 1) {
            return new ApiRespDto<>("failed", "회원 연동에 문제가 발생했습니다.", null);
        }

        return new ApiRespDto<>("success", "연동이 완료되었습니다.", null);
    }
}
















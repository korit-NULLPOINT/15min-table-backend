package com.nullpoint.fifteenmintable.service;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.oauth2.OAuth2MergeReqDto;
import com.nullpoint.fifteenmintable.dto.oauth2.OAuth2SignupReqDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.entity.UserRole;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
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

    @Transactional
    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        if (oAuth2SignupReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");
        if (isBlank(oAuth2SignupReqDto.getEmail()) || isBlank(oAuth2SignupReqDto.getPassword()) || isBlank(oAuth2SignupReqDto.getUsername())
                || isBlank(oAuth2SignupReqDto.getProvider()) || isBlank(oAuth2SignupReqDto.getProviderUserId())) {
            throw new BadRequestException("email/password/username/provider/providerUserId는 필수입니다.");
        }

        if (userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail()).isPresent()) {
            throw new BadRequestException("이미 존재하는 이메일 입니다.");
        }
        if (userRepository.getUserByUsername(oAuth2SignupReqDto.getUsername()).isPresent()) {
            throw new BadRequestException("이미 존재하는 사용자 이름 입니다.");
        }

        if (oAuth2UserRepository
                .getOAuth2UserByProviderAndProviderUserId(oAuth2SignupReqDto.getProvider(), oAuth2SignupReqDto.getProviderUserId())
                .isPresent()) {
            throw new BadRequestException("이미 연동된 OAuth2 계정입니다.");
        }

        User user = oAuth2SignupReqDto.toUserEntity();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        int resultUser = userRepository.addUser(user);
        if (resultUser != 1) throw new RuntimeException("회원 추가에 실패했습니다.");

        Integer userId = user.getUserId();
        if (userId == null) throw new RuntimeException("회원 추가 후 userId 조회 실패");

        int resultRole = userRoleRepository.addUserRole(
                UserRole.builder()
                        .userId(userId)
                        .roleId(3)
                        .build()
        );
        if (resultRole != 1) throw new RuntimeException("회원 권한 추가에 실패했습니다.");

        int resultOAuth2 = oAuth2UserRepository.addOAuth2User(oAuth2SignupReqDto.toOAuth2UserEntity(userId));
        if (resultOAuth2 != 1) throw new RuntimeException("OAuth2 추가에 실패했습니다.");

        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", null);
    }

    @Transactional
    public ApiRespDto<?> merge(OAuth2MergeReqDto oAuth2MergeReqDto) {
        if (oAuth2MergeReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");
        if (isBlank(oAuth2MergeReqDto.getEmail()) || isBlank(oAuth2MergeReqDto.getPassword())
                || isBlank(oAuth2MergeReqDto.getProvider()) || isBlank(oAuth2MergeReqDto.getProviderUserId())) {
            throw new BadRequestException("email/password/provider/providerUserId는 필수입니다.");
        }

        User user = userRepository.getUserByEmail(oAuth2MergeReqDto.getEmail())
                .orElseThrow(() -> new UnauthenticatedException("사용자 정보를 다시 확인해주세요."));

        if (!user.isActive()) {
            throw new UnauthenticatedException("탈퇴처리된 계정입니다.");
        }

        if (!bCryptPasswordEncoder.matches(oAuth2MergeReqDto.getPassword(), user.getPassword())) {
            throw new UnauthenticatedException("사용자 정보를 다시 확인해주세요.");
        }

        if (oAuth2UserRepository
                .getOAuth2UserByProviderAndProviderUserId(oAuth2MergeReqDto.getProvider(), oAuth2MergeReqDto.getProviderUserId())
                .isPresent()) {
            throw new BadRequestException("이미 연동된 OAuth2 계정입니다.");
        }

        int result = oAuth2UserRepository.addOAuth2User(oAuth2MergeReqDto.toEntity(user.getUserId()));
        if (result != 1) throw new RuntimeException("회원 연동에 실패했습니다.");

        return new ApiRespDto<>("success", "연동이 완료되었습니다.", null);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
















package com.nullpoint.fifteenmintable.service;


import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.auth.SigninReqDto;
import com.nullpoint.fifteenmintable.dto.auth.SignupReqDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.entity.UserRole;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRoleRepository;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import com.nullpoint.fifteenmintable.security.auth.store.TokenBlacklistStore;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserAuthService {
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenBlacklistStore tokenBlacklistStore;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${app.user.default-profile-img-url:}")
    private String defaultProfileImgUrl;

    @Transactional
    public ApiRespDto<Void> signup(SignupReqDto signupReqDto) {
        if (signupReqDto == null) {
            throw new BadRequestException("요청 값이 비어있습니다.");
        }
        if (isBlank(signupReqDto.getEmail()) || isBlank(signupReqDto.getPassword()) || isBlank(signupReqDto.getUsername())) {
            throw new BadRequestException("email/password/username은 필수입니다.");
        }
        if (userRepository.getUserByEmail(signupReqDto.getEmail()).isPresent()) {
            throw new BadRequestException("이미 존재하는 이메일 입니다.");
        }
        if (userRepository.getUserByUsername(signupReqDto.getUsername()).isPresent()) {
            throw new BadRequestException("이미 존재하는 사용자 이름 입니다.");
        }

        User user = signupReqDto.toEntity();

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if (isBlank(user.getProfileImgUrl())) {
            if (isBlank(defaultProfileImgUrl)) {
                throw new BadRequestException("기본 프로필 이미지 설정이 필요합니다.");
            }
            user.setProfileImgUrl(defaultProfileImgUrl);
        }

        int r1 = userRepository.addUser(user);
        if (r1 != 1 || user.getUserId() == null) {
            throw new RuntimeException("회원 추가에 실패했습니다.");
        }

        UserRole userRole = UserRole.builder()
                .userId(user.getUserId())
                .roleId(3)
                .build();

        int r2 = userRoleRepository.addUserRole(userRole);
        if (r2 != 1) {
            throw new RuntimeException("회원 권한 추가에 실패했습니다.");
        }

        return new ApiRespDto<>("success", "회원가입이 완료되었습니다.", null);
    }

    public ApiRespDto<String> signin(SigninReqDto signinReqDto) {
        if (signinReqDto == null) {
            throw new BadRequestException("요청 값이 비어있습니다.");
        }
        if (isBlank(signinReqDto.getEmail()) || isBlank(signinReqDto.getPassword())) {
            throw new BadRequestException("email/password는 필수입니다.");
        }

        User user = userRepository.getUserByEmail(signinReqDto.getEmail())
                .orElseThrow(() -> new NotFoundException("사용자 정보를 다시 확인해주세요."));

        if (user.isBanned()) {
            throw new UnauthenticatedException("차단된 계정입니다.");
        }
        if (!user.isActive()) {
            throw new UnauthenticatedException("탈퇴처리된 계정입니다.");
        }

        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            throw new UnauthenticatedException("사용자 정보를 다시 확인해주세요.");
        }

        String accessToken = jwtUtils.generateAccessToken(user.getUserId().toString());
        return new ApiRespDto<>("success", "로그인 성공", accessToken);
    }

    @Transactional
    public ApiRespDto<Void> logout(String accessToken) {

        if (isBlank(accessToken)) {
            return new ApiRespDto<>("success", "로그아웃 되었습니다.", null);
        }

        if (jwtUtils.isBearer(accessToken)) {
            accessToken = jwtUtils.removeBearer(accessToken);
            if (isBlank(accessToken)) {
                return new ApiRespDto<>("success", "로그아웃 되었습니다.", null);
            }
        }

        try {
            Claims claims = jwtUtils.getClaims(accessToken);

            Date exp = claims.getExpiration();
            if (exp == null) {
                // exp가 없다면 블랙리스트 의미가 애매하니 스킵
                return new ApiRespDto<>("success", "로그아웃 되었습니다.", null);
            }

            long ttlSeconds = (exp.getTime() - System.currentTimeMillis()) / 1000L;

            // 만료됐거나 곧 만료될 토큰은 굳이 저장 X
            if (ttlSeconds > 0) {
                tokenBlacklistStore.blacklist(accessToken, ttlSeconds);
            }

        } catch (RuntimeException ignore) {
            // 만료/위조/파싱 실패면 블랙리스트 등록 스킵
            // (그래도 로그아웃 응답은 성공 처리)
        }

        return new ApiRespDto<>("success", "로그아웃 되었습니다.", null);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}












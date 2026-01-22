package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowCountRespDto;
import com.nullpoint.fifteenmintable.dto.user.UserProfileRespDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.repository.FollowRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    public ApiRespDto<UserProfileRespDto> getUserProfile(Integer userId, PrincipalUser principalUser) {
        if (userId == null) {
            throw new BadRequestException("userId는 필수입니다.");
        }

        Integer viewerUserId = (principalUser == null) ? null : principalUser.getUserId();

        UserProfileRespDto resp = userRepository
                .getUserProfile(userId, viewerUserId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        return new ApiRespDto<>("success", "유저 프로필 조회 완료", resp);
    }
}

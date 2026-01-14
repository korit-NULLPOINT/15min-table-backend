package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowStatusRespDto;
import com.nullpoint.fifteenmintable.entity.Follow;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.FollowRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ApiRespDto<?> follow(Integer targetUserId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (targetUserId == null) {
            throw new RuntimeException("targetUserId는 필수입니다.");
        }

        Integer me = principalUser.getUserId();

        if (me.equals(targetUserId)) {
            throw new RuntimeException("자기 자신을 팔로우할 수 없습니다.");
        }

        // 대상 유저 존재 체크
        userRepository.getUserByUserId(targetUserId)
                .orElseThrow(() -> new NotFoundException("대상 유저를 찾을 수 없습니다."));

        // 이미 팔로우 중이면 성공 처리(정책)
        if (followRepository.existsFollow(me, targetUserId)) {
            return new ApiRespDto<>("success", "이미 팔로우 중입니다.", null);
        }

        Follow follow = Follow.builder()
                .followerUserId(me)
                .followingUserId(targetUserId)
                .build();

        // 중복 팔로우(연타/동시 요청) 방지: 선조회 + 유니크 충돌 시 멱등 처리
        try {
            int result = followRepository.addFollow(follow);
            if (result != 1) throw new RuntimeException("팔로우 실패");
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return new ApiRespDto<>("success", "이미 팔로우 중입니다.", null);
        }

        int result = followRepository.addFollow(follow);
        if (result != 1) {
            throw new RuntimeException("팔로우 실패");
        }

        return new ApiRespDto<>("success", "팔로우 완료", null);
    }

    @Transactional
    public ApiRespDto<?> unfollow(Integer targetUserId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (targetUserId == null) {
            throw new RuntimeException("targetUserId는 필수입니다.");
        }

        Integer me = principalUser.getUserId();

        int result = followRepository.deleteFollow(me, targetUserId);

        // 정책: 없는 관계 삭제도 성공 처리(멱등)
        if (result == 0) {
            return new ApiRespDto<>("success", "이미 언팔로우 상태입니다.", null);
        }

        return new ApiRespDto<>("success", "언팔로우 완료", null);
    }

    public ApiRespDto<?> getFollowers(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        Integer me = principalUser.getUserId();

        List<FollowRespDto> list = followRepository.getFollowersByUserId(me);
        return new ApiRespDto<>("success", "팔로워 조회 완료", list);
    }

    public ApiRespDto<?> getFollowings(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        Integer me = principalUser.getUserId();

        List<FollowRespDto> list = followRepository.getFollowingsByUserId(me);
        return new ApiRespDto<>("success", "팔로잉 조회 완료", list);
    }

    public ApiRespDto<?> getFollowStatus(Integer targetUserId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (targetUserId == null) {
            throw new RuntimeException("targetUserId는 필수입니다.");
        }

        boolean isFollowing = followRepository.existsFollow(principalUser.getUserId(), targetUserId);

        return new ApiRespDto<>("success", "팔로우 상태 조회 완료",
                new FollowStatusRespDto(isFollowing));
    }
}

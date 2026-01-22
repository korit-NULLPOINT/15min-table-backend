package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.follow.FollowCountRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowRespDto;
import com.nullpoint.fifteenmintable.entity.Follow;
import com.nullpoint.fifteenmintable.mapper.FollowMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FollowRepository {

    @Autowired
    private FollowMapper followMapper;

    public int addFollow(Follow follow) {
        return followMapper.addFollow(follow);
    }

    public int deleteFollow(Integer followerUserId, Integer followingUserId) {
        return followMapper.deleteFollow(followerUserId, followingUserId);
    }

    public Optional<Follow> getFollow(Integer followerUserId, Integer followingUserId) {
        return followMapper.getFollow(followerUserId, followingUserId);
    }

    public boolean existsFollow(Integer followerUserId, Integer followingUserId) {
        return followMapper.existsFollow(followerUserId, followingUserId);
    }

    public List<FollowRespDto> getFollowersByUserId(Integer userId) {
        return followMapper.getFollowersByUserId(userId);
    }

    public List<FollowRespDto> getFollowingsByUserId(Integer userId) {
        return followMapper.getFollowingsByUserId(userId);
    }

    public Optional<FollowCountRespDto> getFollowCount(Integer userId) {
        return followMapper.getFollowCount(userId);
    }

    public List<Integer> getFollowerUserIdListByFollowingUserId(Integer followingUserId) {
        return followMapper.getFollowerUserIdListByFollowingUserId(followingUserId);
    }

    // 팔로잉 피드/필터 (내가 팔로우한 사람들 글만 보기)
    public List<Integer> getFollowingUserIdListByFollowerUserId(Integer followerUserId) {
        return followMapper.getFollowingUserIdListByFollowerUserId(followerUserId);
    }


}

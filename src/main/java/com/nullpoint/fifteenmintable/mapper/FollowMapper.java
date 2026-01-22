package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.follow.FollowCountRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowRespDto;
import com.nullpoint.fifteenmintable.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FollowMapper {

    int addFollow(Follow follow);
    int deleteFollow(@Param("followerUserId") Integer followerUserId,
                     @Param("followingUserId") Integer followingUserId);
    Optional<Follow> getFollow(@Param("followerUserId") Integer followerUserId,
                               @Param("followingUserId") Integer followingUserId);
    boolean existsFollow(@Param("followerUserId") Integer followerUserId,
                         @Param("followingUserId") Integer followingUserId);
    List<FollowRespDto> getFollowersByUserId(Integer userId);
    List<FollowRespDto> getFollowingsByUserId(Integer userId);
    Optional<FollowCountRespDto> getFollowCount(Integer userId);

    // 알림용 단순 userId목록
    List<Integer> getFollowerUserIdListByFollowingUserId(Integer followingUserId);
    List<Integer> getFollowingUserIdListByFollowerUserId(Integer followerUserId);
}

package com.nullpoint.fifteenmintable.mapper;
import com.nullpoint.fifteenmintable.dto.user.UserProfileRespDto;
import com.nullpoint.fifteenmintable.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    Optional<UserProfileRespDto> getUserProfile(
            @Param("userId") Integer userId,
            @Param("viewerUserId") Integer viewerUserId);
    int addUser(User user);
    int changePassword(User user);
    int changeUsername(User user);
    int changeProfileImg(User user);
    List<User> getUserList();
    int withdraw(Integer userId);
    int deleteUser();
    int banUser(Integer userId);
    int restoreUser(Integer userId);

}

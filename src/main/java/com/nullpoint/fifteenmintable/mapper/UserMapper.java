package com.nullpoint.fifteenmintable.mapper;
import com.nullpoint.fifteenmintable.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    int addUser(User user);
    int changePassword(User user);
    int changeUsername(User user);
    int changeProfileImg(User user);
    List<User> getUserList();
    void deleteUser();
}

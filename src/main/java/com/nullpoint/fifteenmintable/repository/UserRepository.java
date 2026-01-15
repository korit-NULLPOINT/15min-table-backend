package com.nullpoint.fifteenmintable.repository;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    public Optional<User> getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    public int addUser(User user) {
        return userMapper.addUser(user);
    }

    public int changePassword(User user) {
        return userMapper.changePassword(user);
    }

    public int changeUsername(User user) {
        return userMapper.changeUsername(user);
    }

    public int changeProfileImg(User user) {
        return userMapper.changeProfileImg(user);
    }

    public List<User> getUserList() {
        return userMapper.getUserList();
    }

    public int withdraw(Integer userId) {
        return userMapper.withdraw(userId);
    }

    public int deleteUser() {
        return userMapper.deleteUser();
    }
}

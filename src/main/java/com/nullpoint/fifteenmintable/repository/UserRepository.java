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

    public Optional<User> addUser(User user) {
        try {
            int result = userMapper.addUser(user);
            if (result != 1) {
                throw new RuntimeException("회원정보 추가에 실패했습니다.");
            }
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        return Optional.of(user);
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

    public void deleteUser() {
        userMapper.deleteUser();
    }
}

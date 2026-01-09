package com.nullpoint.fifteenmintable.repository;
import com.nullpoint.fifteenmintable.entity.UserRole;
import com.nullpoint.fifteenmintable.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public int addUserRole(UserRole userRole) {
        try {
            return userRoleMapper.addUserRole(userRole);
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateUserRole(UserRole userRole) {
        return userRoleMapper.updateUserRole(userRole);
    }

}

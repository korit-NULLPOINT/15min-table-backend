package com.nullpoint.fifteenmintable.mapper;
import com.nullpoint.fifteenmintable.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    int addUserRole(UserRole userRole);
    int updateUserRole(UserRole userRole);
}

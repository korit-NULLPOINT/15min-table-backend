package com.nullpoint.fifteenmintable.service;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManageService {

    @Autowired
    private UserRepository userRepository;

    public ApiRespDto<?> getUserList(PrincipalUser principalUser) {
        if (principalUser.getUserRoles().stream().noneMatch(userRole -> userRole.getRoleId() == 1)) {
            return new ApiRespDto<>("failed", "접근 권한이 없습니다.", null);
        }

        return new ApiRespDto<>("success", "회원 정보 전체 조회 완료", userRepository.getUserList());
    }
    public ApiRespDto<?> getUserByUsername(String username, PrincipalUser principalUser){
     if (principalUser.getUserRoles().stream().noneMatch(userRole -> userRole.getRoleId() == 1)) {
         return new ApiRespDto<>("failed", "접근 권한이 없습니다.", null);
     }
        Optional<User> foundUser = userRepository.getUserByUsername(username);
     if (foundUser.isEmpty()){
         return new ApiRespDto<>("failed", "해당 회원이 존재 하지 않습니다", null);
     }
        return new ApiRespDto<>("success", "회원 정보 조회 완료ㅕ", foundUser.get());
    }
}

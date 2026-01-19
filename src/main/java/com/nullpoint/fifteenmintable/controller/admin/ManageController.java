package com.nullpoint.fifteenmintable.controller.admin;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("/user/list")
    public ResponseEntity<ApiRespDto<List<User>>> getUserList(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(manageService.getUserList(principalUser));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiRespDto<User>> getUserByUsername(@PathVariable String username, @AuthenticationPrincipal PrincipalUser principalUser){
        return ResponseEntity.ok(manageService.getUserByUsername(username,principalUser));
    }
}

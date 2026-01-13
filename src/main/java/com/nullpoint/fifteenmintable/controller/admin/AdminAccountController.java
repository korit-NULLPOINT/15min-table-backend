package com.nullpoint.fifteenmintable.controller.admin;
import com.nullpoint.fifteenmintable.dto.account.ChangePasswordReqDto;
import com.nullpoint.fifteenmintable.dto.account.ChangeUsernameReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/change/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordReqDto changePasswordReqDto, @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(accountService.changePassword(changePasswordReqDto, principalUser));
    }

    @PostMapping("/change/username")
    public ResponseEntity<?> changeUsername(@RequestBody ChangeUsernameReqDto changeUsernameReqDto, @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(accountService.changeUsername(changeUsernameReqDto, principalUser));
    }
}

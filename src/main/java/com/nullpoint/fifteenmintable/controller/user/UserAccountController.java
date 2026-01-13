package com.nullpoint.fifteenmintable.controller.user;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.account.ChangePasswordReqDto;
import com.nullpoint.fifteenmintable.dto.account.ChangeProfileImgReqDto;
import com.nullpoint.fifteenmintable.dto.account.ChangeUsernameReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/account")
public class UserAccountController {

    @Autowired
    private AccountService userAccountService;

    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(new ApiRespDto<>("success", "회원 조회 완료", principalUser));
    }

    @PostMapping("/change/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordReqDto changePasswordReqDto, @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(userAccountService.changePassword(changePasswordReqDto, principalUser));
    }

    @PostMapping("/change/username")
    public ResponseEntity<?> changeUsername(@RequestBody ChangeUsernameReqDto changeUsernameReqDto, @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(userAccountService.changeUsername(changeUsernameReqDto, principalUser));
    }

    @PostMapping("/change/profileImg")
    public ResponseEntity<?> changeProfileImg(@RequestBody ChangeProfileImgReqDto profileImgReqDto, @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(userAccountService.changeProfileImg(profileImgReqDto, principalUser));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(userAccountService.withdraw(principalUser));
    }
}

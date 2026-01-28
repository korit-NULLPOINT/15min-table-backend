package com.nullpoint.fifteenmintable.service;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.account.ChangePasswordReqDto;
import com.nullpoint.fifteenmintable.dto.account.ChangeProfileImgReqDto;
import com.nullpoint.fifteenmintable.dto.account.ChangeUsernameReqDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.CommentRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public ApiRespDto<Void> changePassword(ChangePasswordReqDto changePasswordReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");
        if (changePasswordReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");
        if (changePasswordReqDto.getUserId() == null) throw new BadRequestException("userId는 필수입니다.");
        if (isBlank(changePasswordReqDto.getPassword()) || isBlank(changePasswordReqDto.getNewPassword())) {
            throw new BadRequestException("password/newPassword는 필수입니다.");
        }

        if (!changePasswordReqDto.getUserId().equals(principalUser.getUserId())) {
            throw new ForbiddenException("잘못된 접근입니다.");
        }

        User user = userRepository.getUserByUserId(changePasswordReqDto.getUserId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원정보입니다."));

        if (!bCryptPasswordEncoder.matches(changePasswordReqDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (bCryptPasswordEncoder.matches(changePasswordReqDto.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("새 비밀번호가 기존 비밀번호와 일치합니다.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordReqDto.getNewPassword()));

        int result = userRepository.changePassword(user);
        if (result != 1) {
            throw new RuntimeException("비밀번호 변경에 실패했습니다.");
        }

        return new ApiRespDto<>("success", "비밀번호가 변경되었습니다. 다시 로그인 해주세요.", null);
    }

    @Transactional
    public ApiRespDto<Void> changeUsername(ChangeUsernameReqDto changeUsernameReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");
        if (changeUsernameReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");
        if (changeUsernameReqDto.getUserId() == null) throw new BadRequestException("userId는 필수입니다.");
        if (isBlank(changeUsernameReqDto.getUsername())) throw new BadRequestException("username은 필수입니다.");

        if (!changeUsernameReqDto.getUserId().equals(principalUser.getUserId())) {
            throw new ForbiddenException("잘못된 접근입니다.");
        }

        User user = userRepository.getUserByUserId(changeUsernameReqDto.getUserId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원정보입니다."));

        userRepository.getUserByUsername(changeUsernameReqDto.getUsername())
                .ifPresent(u -> {
                    throw new BadRequestException("이미 존재하는 사용자 이름입니다.");
                });

        user.setUsername(changeUsernameReqDto.getUsername());

        int result = userRepository.changeUsername(user);
        if (result != 1) {
            throw new RuntimeException("사용자 이름 변경에 실패했습니다.");
        }

        return new ApiRespDto<>("success", "사용자 이름이 변경되었습니다.", null);
    }

    @Transactional
    public ApiRespDto<Void> changeProfileImg(ChangeProfileImgReqDto changeProfileImgReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");
        if (changeProfileImgReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");
        if (changeProfileImgReqDto.getUserId() == null) throw new BadRequestException("userId는 필수입니다.");
        if (isBlank(changeProfileImgReqDto.getProfileImgUrl())) throw new BadRequestException("profileImg는 필수입니다.");

        if (!changeProfileImgReqDto.getUserId().equals(principalUser.getUserId())) {
            throw new ForbiddenException("잘못된 접근입니다.");
        }

        User user = userRepository.getUserByUserId(changeProfileImgReqDto.getUserId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원정보입니다."));

        user.setProfileImgUrl(changeProfileImgReqDto.getProfileImgUrl());

        int result = userRepository.changeProfileImg(user);
        if (result != 1) {
            throw new RuntimeException("사용자 프로필 이미지 변경에 실패했습니다.");
        }

        return new ApiRespDto<>("success", "사용자 프로필 이미지가 변경되었습니다.", null);
    }

    @Transactional
    public ApiRespDto<Void> withdraw(PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");

        User user = userRepository.getUserByUserId(principalUser.getUserId())
                .orElseThrow(() -> new NotFoundException("회원정보가 존재하지 않습니다."));

        if (!user.isActive()) {
            throw new BadRequestException("이미 탈퇴 처리된 계정입니다.");
        }

        int result = userRepository.withdraw(user.getUserId());
        if (result != 1) {
            throw new RuntimeException("탈퇴처리에 실패했습니다.");
        }

        return new ApiRespDto<>("success", "탈퇴처리가 완료되었습니다. 90일 이후 회원정보가 완전히 삭제됩니다.", null);
    }

    @Transactional
    public int deleteUser() {

        commentRepository.deleteRecipeCommentsOfPurgeTargets();

        return userRepository.deleteUser();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}









package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowCountRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowRespDto;
import com.nullpoint.fifteenmintable.dto.follow.FollowStatusRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<ApiRespDto<Void>> follow(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(followService.follow(targetUserId, principalUser));
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiRespDto<Void>> unfollow(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(followService.unfollow(targetUserId, principalUser));
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiRespDto<List<FollowRespDto>>> getFollowers(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(followService.getFollowers(principalUser));
    }

    @GetMapping("/followings")
    public ResponseEntity<ApiRespDto<List<FollowRespDto>>> getFollowings(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(followService.getFollowings(principalUser));
    }

    @GetMapping("/status/{targetUserId}")
    public ResponseEntity<ApiRespDto<FollowStatusRespDto>> getFollowStatus(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(followService.getFollowStatus(targetUserId, principalUser));
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiRespDto<FollowCountRespDto>> getFollowCount(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(followService.getFollowCount(userId));
    }
}

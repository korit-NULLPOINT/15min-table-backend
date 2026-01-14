package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<?> follow(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(followService.follow(targetUserId, principalUser));
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<?> unfollow(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(followService.unfollow(targetUserId, principalUser));
    }

    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(followService.getFollowers(principalUser));
    }

    @GetMapping("/followings")
    public ResponseEntity<?> getFollowings(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(followService.getFollowings(principalUser));
    }

    @GetMapping("/status/{targetUserId}")
    public ResponseEntity<?> getFollowStatus(
            @PathVariable Integer targetUserId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(followService.getFollowStatus(targetUserId, principalUser));
    }
}

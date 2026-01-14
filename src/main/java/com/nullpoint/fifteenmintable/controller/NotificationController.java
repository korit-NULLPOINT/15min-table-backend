package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * - 최초 모달: /notifications?size=5
     * - 무한스크롤: /notifications?cursor=123&size=20
     */
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.getNotifications(cursor, size, principalUser));
    }

    /**
     * 미읽음 개수(뱃지)
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principalUser));
    }

    /**
     * 단건 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Integer notificationId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, principalUser));
    }

    /**
     * 모두 읽음 처리
     */
    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.markAllAsRead(principalUser));
    }
}

package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.notification.NotificationRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.NotificationService;
import com.nullpoint.fifteenmintable.service.NotificationSseService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationSseService notificationSseService;

    @Hidden // orval 에서 제외
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalUser principalUser) {
        // 로그인 안 되면 시큐리티/엔트리포인트에서 컷
        return notificationSseService.subscribe(principalUser.getUserId());
    }

    /**
     * - 최초 모달: /notifications?size=5
     * - 무한스크롤: /notifications?cursor=123&size=20
     */
    @GetMapping("")
    public ResponseEntity<ApiRespDto<List<NotificationRespDto>>> getNotifications(
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
    public ResponseEntity<ApiRespDto<Integer>> getUnreadCount(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principalUser));
    }

    /**
     * 단건 읽음 처리
     */
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiRespDto<Void>> markAsRead(
            @PathVariable Integer notificationId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, principalUser));
    }

    /**
     * 모두 읽음 처리
     */
    @PostMapping("/read-all")
    public ResponseEntity<ApiRespDto<Void>> markAllAsRead(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(notificationService.markAllAsRead(principalUser));
    }
}

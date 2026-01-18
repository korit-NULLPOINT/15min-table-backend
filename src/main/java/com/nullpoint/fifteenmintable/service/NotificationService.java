package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.notification.NotificationRespDto;
import com.nullpoint.fifteenmintable.entity.Notification;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.FollowRepository;
import com.nullpoint.fifteenmintable.repository.NotificationRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationSseService notificationSseService;


    @Transactional
    public void createRecipePostNotifications(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (recipeId == null) {
            throw new RuntimeException("recipeId는 필수입니다.");
        }

        Integer actorUserId = principalUser.getUserId();

        List<Integer> receiverIds = followRepository.getFollowerUserIdListByFollowingUserId(actorUserId);

        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }

        for (Integer receiverUserId : receiverIds) {
            Notification notification = Notification.builder()
                    .receiverUserId(receiverUserId)
                    .actorUserId(actorUserId)
                    .notificationType("RECIPE_POST")
                    .recipeId(recipeId)
                    .commentId(null)
                    .build();

            notificationRepository.createNotification(notification);

            notificationSseService.pushToUser(
                    receiverUserId,
                    // payload는 Map으로 가도 되고 DTO 만들어도 됨
                    java.util.Map.of(
                            "notificationId", notification.getNotificationId(),
                            "type", "RECIPE_POST",
                            "recipeId", recipeId,
                            "actorUserId", actorUserId
                    )
            );
        }
    }

    /**
     * - cursor: 마지막으로 받은 notificationId (없으면 null)
     * - size: 기본 5, 최대 20 제한
     */
    public ApiRespDto<?> getNotifications(Integer cursor, Integer size, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        int s = (size == null) ? 5 : size;
        if (s < 1) s = 1;
        if (s > 20) s = 20;

        List<NotificationRespDto> list =
                notificationRepository.getNotifications(principalUser.getUserId(), cursor, s);

        if (list == null) list = Collections.emptyList();

        return new ApiRespDto<>("success", "알림 조회 완료", list);
    }

    public ApiRespDto<?> getUnreadCount(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        int count = notificationRepository.getUnreadCount(principalUser.getUserId());
        return new ApiRespDto<>("success", "미읽음 개수 조회 완료", count);
    }

    @Transactional
    public ApiRespDto<?> markAsRead(Integer notificationId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (notificationId == null) {
            throw new RuntimeException("notificationId는 필수입니다.");
        }

        int updated = notificationRepository.markAsRead(notificationId, principalUser.getUserId());

        if (updated == 0) {
            throw new NotFoundException("알림을 찾을 수 없습니다.");
        }

        return new ApiRespDto<>("success", "읽음 처리 완료", null);
    }

    /**
     * 모두 읽음 처리
     * - 0건이어도 성공(이미 다 읽음)
     */
    @Transactional
    public ApiRespDto<?> markAllAsRead(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        notificationRepository.markAllAsRead(principalUser.getUserId());
        return new ApiRespDto<>("success", "모두 읽음 처리 완료", null);
    }
}

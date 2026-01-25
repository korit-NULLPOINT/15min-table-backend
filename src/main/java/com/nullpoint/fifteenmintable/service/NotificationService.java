package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.notification.NotificationPushRespDto;
import com.nullpoint.fifteenmintable.dto.notification.NotificationRespDto;
import com.nullpoint.fifteenmintable.entity.Notification;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.FollowRepository;
import com.nullpoint.fifteenmintable.repository.NotificationRepository;
import com.nullpoint.fifteenmintable.repository.RecipeRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private NotificationSseService notificationSseService;


    @Transactional
    public void createRecipePostNotifications(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (recipeId == null) {
            throw new BadRequestException("recipeId는 필수입니다.");
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

            afterCommit(() -> {
                try {
                    notificationSseService.pushToUser(
                            receiverUserId,
                            NotificationPushRespDto.builder()
                                    .notificationId(notification.getNotificationId())
                                    .type("RECIPE_POST")
                                    .actorUserId(actorUserId)
                                    .recipeId(recipeId)
                                    .commentId(null)
                                    .build()
                    );
                } catch (Exception e) {
                    System.out.println("[SSE] push failed: " + e.getMessage());
                }
            });
        }
    }

    @Transactional
    public void createCommentNotification(Integer recipeId, Integer commentId, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");
        if (commentId == null) throw new BadRequestException("commentId는 필수입니다.");

        Integer actorUserId = principalUser.getUserId();

        Recipe recipe = recipeRepository.getRecipeEntityById(recipeId)
                .orElseThrow(() -> new NotFoundException("레시피를 찾을 수 없습니다."));

        Integer receiverUserId = recipe.getUserId();

        if (receiverUserId == null || receiverUserId.equals(actorUserId)) return;

        Notification notification = Notification.builder()
                .receiverUserId(receiverUserId)
                .actorUserId(actorUserId)
                .notificationType("COMMENT")
                .recipeId(recipeId)
                .commentId(commentId)
                .build();

        notificationRepository.createNotification(notification);
    }


    @Transactional
    public void createFollowNotification(Integer targetUserId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (targetUserId == null) {
            throw new BadRequestException("targetUserId는 필수입니다.");
        }

        Integer actorUserId = principalUser.getUserId();

        Notification notification = Notification.builder()
                .receiverUserId(targetUserId)
                .actorUserId(actorUserId)
                .notificationType("FOLLOW")
                .recipeId(null)
                .commentId(null)
                .build();

        notificationRepository.createNotification(notification);

        afterCommit(() -> {
            try {
                notificationSseService.pushToUser(
                        targetUserId,
                        NotificationPushRespDto.builder()
                                .notificationId(notification.getNotificationId())
                                .type("FOLLOW")
                                .actorUserId(actorUserId)
                                .recipeId(null)
                                .commentId(null)
                                .build()
                );
            } catch (Exception e) {
                System.out.println("[SSE] push failed: " + e.getMessage());
            }
        });
    }

    /**
     * - cursor: 마지막으로 받은 notificationId (없으면 null)
     * - size: 기본 5, 최대 20 제한
     */
    public ApiRespDto<List<NotificationRespDto>> getNotifications(
            Integer cursor, Integer size, String mode, PrincipalUser principalUser
    ) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        int s = (size == null) ? 5 : size;
        if (s < 1) s = 1;
        if (s > 20) s = 20;

        String m = (mode == null) ? "UNREAD" : mode.trim().toUpperCase();
        if (!m.equals("UNREAD") && !m.equals("READ")) {
            throw new BadRequestException("mode는 UNREAD 또는 READ만 가능합니다.");
        }

        final int READ_KEEP_DAYS = 14;

        List<NotificationRespDto> list =
                notificationRepository.getNotifications(principalUser.getUserId(), cursor, s, m, READ_KEEP_DAYS);

        if (list == null) list = Collections.emptyList();

        return new ApiRespDto<>("success", "알림 조회 완료", list);
    }

    public ApiRespDto<Integer> getUnreadCount(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        int count = notificationRepository.getUnreadCount(principalUser.getUserId());
        return new ApiRespDto<>("success", "미읽음 개수 조회 완료", count);
    }

    @Transactional
    public ApiRespDto<Void> markAsRead(Integer notificationId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (notificationId == null) {
            throw new BadRequestException("notificationId는 필수입니다.");
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
    public ApiRespDto<Void> markAllAsRead(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        notificationRepository.markAllAsRead(principalUser.getUserId());
        return new ApiRespDto<>("success", "모두 읽음 처리 완료", null);
    }

    private void afterCommit(Runnable r) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            r.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                r.run();
            }
        });
    }
}

package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.repository.CommentRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void 댓글_작성자가_아니면_삭제에_실패한다() {
        // given
        Integer commentId = 10;
        Integer commentOwnerUserId = 1;
        Integer loginUserId = 2;

        PrincipalUser principalUser = PrincipalUser.builder()
                .userId(loginUserId)
                .build();

        CommentRespDto foundComment = new CommentRespDto(
                commentId,
                "RECIPE",
                100,
                commentOwnerUserId,
                "작성자",
                "레시피 제목",
                "댓글 내용",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(commentRepository.getCommentByCommentId(commentId))
                .thenReturn(Optional.of(foundComment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(commentId, principalUser))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");

        verify(commentRepository, never()).deleteComment(commentId);
    }
}
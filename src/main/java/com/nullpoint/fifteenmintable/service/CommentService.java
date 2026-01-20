package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.CommentRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ApiRespDto<Comment> addComment(AddCommentReqDto addCommentReqDto, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        if (addCommentReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");
        if (addCommentReqDto.getRecipeId() == null) throw new BadRequestException("recipeId는 필수입니다.");
        if (addCommentReqDto.getContent() == null || addCommentReqDto.getContent().trim().isEmpty()) {
            throw new BadRequestException("댓글 내용은 필수입니다.");
        }

        Integer userId = principalUser.getUserId();

        Comment comment = addCommentReqDto.toEntity(userId);
        int result = commentRepository.addComment(comment);

        if (result != 1) {
            throw new RuntimeException("댓글 추가 실패");
        }

        notificationService.createCommentNotification(
                addCommentReqDto.getRecipeId(),
                comment.getCommentId(),
                principalUser
        );

        return new ApiRespDto<>("success", "댓글 추가 완료", comment);
    }

    public ApiRespDto<List<CommentRespDto>> getCommentListByRecipeId(Integer recipeId) {
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");

        List<CommentRespDto> list = commentRepository.getCommentListByRecipeId(recipeId);
        return new ApiRespDto<>("success", "레시피 댓글 목록 조회 완료", list);
    }

    public ApiRespDto<List<CommentRespDto>> getCommentListByUserId(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        Integer userId = principalUser.getUserId();
        List<CommentRespDto> list = commentRepository.getCommentListByUserId(userId);

        return new ApiRespDto<>("success", "내 댓글 목록 조회 완료", list);
    }

    /**
     * 단건조회로 작성자 확인 후 삭제
     */
    public ApiRespDto<Void> deleteComment(Integer commentId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (commentId == null) throw new BadRequestException("commentId는 필수입니다.");

        Integer userId = principalUser.getUserId();

        // 1) 댓글 존재 확인 + 작성자 확인
        CommentRespDto found = commentRepository.getCommentByCommentId(commentId)
                .orElseThrow(() -> new NotFoundException("삭제할 댓글이 없습니다."));

        // 2) 권한 체크
        if (found.getUserId() == null || !found.getUserId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        // 3) 삭제
        int result = commentRepository.deleteComment(commentId);
        if (result != 1) {
            throw new RuntimeException("댓글 삭제 실패");
        }

        return new ApiRespDto<>("success", "댓글 삭제 완료", null);
    }
}

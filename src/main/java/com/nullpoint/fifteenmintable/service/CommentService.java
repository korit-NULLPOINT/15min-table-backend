package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.dto.comment.DeleteCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.ModifyCommentReqDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.CommentRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    public ApiRespDto<?> addComment(AddCommentReqDto addCommentReqDto, PrincipalUser principalUser) {

        if (principalUser == null) {
            throw new UnauthenticatedException("로그인 해주세요.");
        }

        Comment comment = addCommentReqDto.toEntity();
        comment.setUserId(principalUser.getUserId());

        int result = commentRepository.addComment(comment);
        if (result != 1) {
            throw new RuntimeException("댓글 추가 실패");
        }

        return new ApiRespDto<>("success", "댓글 추가 완료", comment);
    }


    public ApiRespDto<?> getCommentByCommentId(Integer commentId) {
        CommentRespDto comment = commentRepository.getCommentByCommentId(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 댓글입니다."));

        return new ApiRespDto<>("success", "댓글 조회 완료", comment);
    }

    public ApiRespDto<?> getCommentList() {

        List<CommentRespDto> commentList = commentRepository.getCommentList();

        return new ApiRespDto<>("success", "댓글 조회 완료", commentList);
    }

    public ApiRespDto<?> getCommentListByUserId(Integer userId) {

        List<CommentRespDto> commentList = commentRepository.getCommentListByUserId(userId);

        return new ApiRespDto<>("success", "댓글 조회 완료", commentList);
    }

    public ApiRespDto<?> modifyComment(ModifyCommentReqDto modifyCommentReqDto, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인 해주세요.");
        }

        CommentRespDto found = commentRepository.getCommentByCommentId(modifyCommentReqDto.getCommentId())
                .orElseThrow(() -> new NotFoundException("존재하지 않은 댓글입니다."));

        if (!found.getUserId().equals(principalUser.getUserId())) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        Comment comment = modifyCommentReqDto.toEntity();

        comment.setUserId(principalUser.getUserId()); // userId를 클라에서 받지 말고 토큰 기준으로

        int result = commentRepository.modifyComment(comment);

        if (result != 1) {
            throw new RuntimeException("댓글 수정 실패");
        }

        // 수정 후 최신 데이터 내려주고 싶으면 재조회해서 반환
        CommentRespDto updated = commentRepository.getCommentByCommentId(modifyCommentReqDto.getCommentId())
                .orElseThrow(() -> new NotFoundException("수정된 댓글을 조회할 수 없습니다."));

        return new ApiRespDto<>("success", "댓글 수정 완료", updated);
    }

    public ApiRespDto<?> deleteComment(DeleteCommentReqDto deleteCommentReqDto, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인 해주세요.");
        }

        CommentRespDto found = commentRepository.getCommentByCommentId(deleteCommentReqDto.getCommentId())
                .orElseThrow(() -> new NotFoundException("존재하지 않은 댓글입니다."));

        if (!found.getUserId().equals(principalUser.getUserId())) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        int result = commentRepository.deleteComment(deleteCommentReqDto.getCommentId());

        if (result != 1) {
            throw new RuntimeException("댓글 삭제 실패");
        }

        return new ApiRespDto<>("success", "댓글 삭제 완료", null);
    }
}

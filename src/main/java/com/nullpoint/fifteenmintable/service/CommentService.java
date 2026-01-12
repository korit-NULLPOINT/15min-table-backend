package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.dto.comment.ModifyCommentReqDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.entity.User;
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
        Optional<User> foundUser= userRepository.getUserByUserId(principalUser.getUserId());

        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "존재 하지 않은 유저 입니다.", null);
        }

        Optional<Comment> optionalComment = commentRepository.addComment(addCommentReqDto.toEntity());

        return new ApiRespDto<>("success", "댓글 추가 완료", optionalComment.get());
    }

    public ApiRespDto<?> getCommentByCommentId(Integer commentId) {
        Optional<CommentRespDto> foundComment = commentRepository.getCommentByCommentId(commentId);

        if (foundComment.isEmpty()) {
            return new ApiRespDto<>("failed", "존재 하지 않은 댓글 입니다.", null);
        }

        Optional<CommentRespDto> optionalComment = commentRepository.getCommentByCommentId(commentId);

        return new ApiRespDto<>("success", "댓글 조회 완료", optionalComment.get());
    }

    public ApiRespDto<?> getCommentList() {

        List<CommentRespDto> CommentList = commentRepository.getCommentList();

        return new ApiRespDto<>("success", "댓글 조회 완료", CommentList);
    }

    public ApiRespDto<?> getCommentListByUserId(Integer userId) {

        Optional<User> foundUser = userRepository.getUserByUserId(userId);

        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "존재 하지 않은 유저 입니다.", null);
        }

        List<CommentRespDto> CommentList = commentRepository.getCommentListByUserId(userId);

        return new ApiRespDto<>("success", "댓글 조회 완료", CommentList);
    }

    public ApiRespDto<?> modifyComment(ModifyCommentReqDto modifyCommentReqDto) {
        Optional<CommentRespDto> foundComment = commentRepository.getCommentByCommentId(modifyCommentReqDto.getCommentId());
        if (foundComment.isEmpty()) {
            return new ApiRespDto<>("failed", "존재 하지 않은 댓글 입니다.", null);

        }

        int result = commentRepository.modifyComment(modifyCommentReqDto.toEntity());

        if (result != 1) {
            return new ApiRespDto<>("failed", "댓글 수정 중 오류 발생", null);
        }

        return new ApiRespDto<>("success", "댓글 수정 완료", modifyCommentReqDto);
    }

}

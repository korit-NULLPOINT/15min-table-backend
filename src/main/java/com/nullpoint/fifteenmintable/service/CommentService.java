package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.comment.AddCommentDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.CommentRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    public ApiRespDto<?> addComment(AddCommentDto addCommentDto) {
        Optional<User> foundUser= userRepository.getUserByUserId(addCommentDto.getUserId());

        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "존재 하지 않은 유저 입니다.", null);
        }

        Optional<Comment> optionalComment = commentRepository.addComment(addCommentDto.toEntity());

        return new ApiRespDto<>("success", "댓글 추가 완료", optionalComment.get());
    }

}

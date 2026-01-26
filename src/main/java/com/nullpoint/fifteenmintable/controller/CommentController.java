package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 레시피 댓글 작성
    @PostMapping("/add/recipe/{recipeId}")
    public ResponseEntity<ApiRespDto<Comment>> addRecipeComment(
            @PathVariable Integer recipeId,
            @RequestBody AddCommentReqDto addCommentReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        addCommentReqDto.setTargetType("RECIPE");
        addCommentReqDto.setTargetId(recipeId);
        return ResponseEntity.ok(commentService.addComment(addCommentReqDto, principalUser));
    }

    @PostMapping("/add/post/{postId}")
    public ResponseEntity<ApiRespDto<Comment>> addPostComment(
            @PathVariable Integer postId,
            @RequestBody AddCommentReqDto addCommentReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        addCommentReqDto.setTargetType("POST");
        addCommentReqDto.setTargetId(postId);
        return ResponseEntity.ok(commentService.addComment(addCommentReqDto, principalUser));
    }

    @GetMapping("/list/{recipeId}")
    public ResponseEntity<ApiRespDto<List<CommentRespDto>>> getCommentListByRecipeId(
            @PathVariable Integer recipeId
    ) {
        return ResponseEntity.ok(commentService.getCommentListByRecipeId(recipeId));
    }

    @GetMapping("/my/list")
    public ResponseEntity<ApiRespDto<List<CommentRespDto>>> getMyCommentList(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.getCommentListByUserId(principalUser));
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ApiRespDto<Void>> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, principalUser));
    }
}

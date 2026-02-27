package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.ratelimit.annotation.RateLimit;
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
    @RateLimit(millis = 500, scope = RateLimit.Scope.USER, key = "comment_add")
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
    @RateLimit(millis = 500, scope = RateLimit.Scope.USER, key = "comment_add")
    public ResponseEntity<ApiRespDto<Comment>> addPostComment(
            @PathVariable Integer postId,
            @RequestBody AddCommentReqDto addCommentReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        addCommentReqDto.setTargetType("POST");
        addCommentReqDto.setTargetId(postId);
        return ResponseEntity.ok(commentService.addComment(addCommentReqDto, principalUser));
    }

    @GetMapping("/list/recipe/{recipeId}")
    public ResponseEntity<ApiRespDto<List<CommentRespDto>>> getRecipeCommentListByTarget(
            @PathVariable Integer recipeId
    ) {
        return ResponseEntity.ok(commentService.getCommentListByTarget("RECIPE", recipeId));
    }

    @GetMapping("/list/post/{postId}")
    public ResponseEntity<ApiRespDto<List<CommentRespDto>>> getPostCommentListByTarget(
            @PathVariable Integer postId
    ) {
        return ResponseEntity.ok(commentService.getCommentListByTarget("POST", postId));
    }

    @GetMapping("/my/list")
    public ResponseEntity<ApiRespDto<List<CommentRespDto>>> getMyCommentList(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.getCommentListByUserId(principalUser));
    }

    @DeleteMapping("/delete/{commentId}")
    @RateLimit(millis = 500, scope = RateLimit.Scope.USER, key = "comment_delete")
    public ResponseEntity<ApiRespDto<Void>> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, principalUser));
    }
}

package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/add")
    public ResponseEntity<?> addComment(
            @RequestBody AddCommentReqDto addCommentReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.addComment(addCommentReqDto, principalUser));
    }

    // 레시피별 댓글 목록
    @GetMapping("/list/{recipeId}")
    public ResponseEntity<?> getCommentListByRecipeId(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(commentService.getCommentListByRecipeId(recipeId));
    }

    // 내 댓글 목록 (마이페이지 용)
    @GetMapping("/my/list")
    public ResponseEntity<?> getMyCommentList(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.getCommentListByUserId(principalUser));
    }

    // 댓글 삭제 (권한 체크는 서비스에서 commentId 단건조회로 처리)
    @PostMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, principalUser));
    }
}

package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.comment.AddCommentReqDto;
import com.nullpoint.fifteenmintable.dto.comment.ModifyCommentReqDto;
import com.nullpoint.fifteenmintable.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody AddCommentReqDto addCommentReqDto) {
        return ResponseEntity.ok(commentService.addComment(addCommentReqDto));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCommentList() {
        return ResponseEntity.ok(commentService.getCommentList());
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentList(@PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getCommentByCommentId(commentId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCommentListByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(commentService.getCommentListByUserId(userId));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modifyComment(@RequestBody ModifyCommentReqDto modifyCommentReqDto) {
        return ResponseEntity.ok(commentService.modifyComment(modifyCommentReqDto));
    }
}

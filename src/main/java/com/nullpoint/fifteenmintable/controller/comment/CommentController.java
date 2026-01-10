package com.nullpoint.fifteenmintable.controller.comment;

import com.nullpoint.fifteenmintable.dto.comment.AddCommentDto;
import com.nullpoint.fifteenmintable.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody AddCommentDto addCommentDto) {
        return ResponseEntity.ok(commentService.addComment(addCommentDto));
    }
}

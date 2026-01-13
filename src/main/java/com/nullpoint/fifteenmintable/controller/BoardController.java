package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.Board.BoardCreateReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    /**
     * 게시물 생성
     */
    @PostMapping
    public ResponseEntity<?> addBoard(
            @RequestBody BoardCreateReqDto boardCreateReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(
                boardService.addBoard(boardCreateReqDto, principalUser)
        );
    }

    /**
     * 게시물 전체 조회 (로그인 필수)
     */
    @GetMapping
    public ResponseEntity<?> getBoardList(
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(
                boardService.getBoardList()
        );
    }

    /**
     * 게시물 삭제 (작성자만 가능, 실제 DELETE)
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> removeBoard(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Integer boardId
    ) {
        return ResponseEntity.ok(
                boardService.removeBoard(boardId, principalUser)
        );
    }
}

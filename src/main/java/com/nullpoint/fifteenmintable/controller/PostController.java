package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.CursorRespDto;
import com.nullpoint.fifteenmintable.dto.post.AddPostReqDto;
import com.nullpoint.fifteenmintable.dto.post.ModifyPostReqDto;
import com.nullpoint.fifteenmintable.dto.post.PostDetailRespDto;
import com.nullpoint.fifteenmintable.dto.post.PostListRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.PostService;
import com.nullpoint.fifteenmintable.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board/{boardId}/posts")
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private PostService postService;

    private final CommonUtil commonUtil;

    @GetMapping("/list")
    public ApiRespDto<CursorRespDto<PostListRespDto>> getPostList(
            @PathVariable Integer boardId,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType
    ) {
        return postService.getPostListByCursor(boardId, size, cursor, keyword, searchType);
    }

    @GetMapping("/detail/{postId}")
    public ApiRespDto<PostDetailRespDto> getPostDetail(
            @PathVariable Integer boardId,
            @PathVariable Integer postId,
            HttpServletRequest request
    ) {
        String userIp = commonUtil.getClientIp(request);
        return postService.getPostDetail(boardId, postId, userIp);
    }

    @PostMapping("/add")
    public ApiRespDto<Integer> addPost(
            @PathVariable Integer boardId,
            @RequestBody AddPostReqDto addPostReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return postService.addPost(boardId, addPostReqDto, principalUser);
    }

    @PostMapping("/modify/{postId}")
    public ApiRespDto<Void> modifyPost(
            @PathVariable Integer boardId,
            @PathVariable Integer postId,
            @RequestBody ModifyPostReqDto modifyPostReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return postService.modifyPost(boardId, postId, modifyPostReqDto, principalUser);
    }

    @PostMapping("/remove/{postId}")
    public ApiRespDto<Void> deletePost(
            @PathVariable Integer boardId,
            @PathVariable Integer postId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return postService.deletePost(boardId, postId, principalUser);
    }
}

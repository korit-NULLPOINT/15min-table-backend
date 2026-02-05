package com.nullpoint.fifteenmintable.controller.post;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.CursorRespDto;
import com.nullpoint.fifteenmintable.dto.post.PostListRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.PostService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class UserPostController {

    @Autowired
    private PostService postService;

    @GetMapping("/user/{userId}")
    public ApiRespDto<CursorRespDto<PostListRespDto>> getPostListByUserIdByCursor(
            @PathVariable Integer userId,

            @Parameter(description = "한 번에 가져올 개수(기본 20, 최대 50)")
            @RequestParam(required = false) Integer size,

            @Parameter(description = "커서(형식: createDt|postId)")
            @RequestParam(required = false) String cursor
    ) {
        return postService.getPostListByUserIdByCursor(userId, size, cursor);
    }

    @GetMapping("/my")
    public ApiRespDto<CursorRespDto<PostListRespDto>> getMyPostListByCursor(
            @Parameter(hidden = true) PrincipalUser principalUser,

            @Parameter(description = "한 번에 가져올 개수(기본 20, 최대 50)")
            @RequestParam(required = false) Integer size,

            @Parameter(description = "커서(형식: createDt|postId)")
            @RequestParam(required = false) String cursor
    ) {
        return postService.getMyPostListByCursor(size, cursor, principalUser);
    }
}

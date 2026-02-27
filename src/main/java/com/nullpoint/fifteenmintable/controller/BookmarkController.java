package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.bookmark.BookmarkRespDto;
import com.nullpoint.fifteenmintable.ratelimit.annotation.RateLimit;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeBookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    @Autowired
    private RecipeBookmarkService recipeBookmarkService;

    @PostMapping("/{recipeId}")
    @RateLimit(millis = 300, scope = RateLimit.Scope.USER, key = "bookmark_action")
    public ResponseEntity<ApiRespDto<Void>> addBookmark(
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeBookmarkService.addBookmark(recipeId, principalUser));
    }

    @DeleteMapping("/{recipeId}")
    @RateLimit(millis = 300, scope = RateLimit.Scope.USER, key = "bookmark_action")
    public ResponseEntity<ApiRespDto<Void>> deleteBookmark(
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeBookmarkService.deleteBookmark(recipeId, principalUser));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<ApiRespDto<Boolean>> existsByRecipeId (
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeBookmarkService.existsByRecipeId(recipeId, principalUser));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiRespDto<List<BookmarkRespDto>>> getBookmarkListByUserId (@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(recipeBookmarkService.getBookmarkListByUserId(principalUser));
    }
}

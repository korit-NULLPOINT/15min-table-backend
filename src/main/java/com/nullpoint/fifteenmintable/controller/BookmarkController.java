package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeBookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    @Autowired
    private RecipeBookmarkService recipeBookmarkService;

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> addBookmark(
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeBookmarkService.addBookmark(recipeId, principalUser));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> deleteBookmark (
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeBookmarkService.deleteBookmark(recipeId, principalUser));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> existsByRecipeId (
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeBookmarkService.existsByRecipeId(recipeId, principalUser));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getBookmarkListByUserId (@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(recipeBookmarkService.getBookmarkListByUserId(principalUser));
    }
}

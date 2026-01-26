package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeListPageRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class UserRecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiRespDto<RecipeListPageRespDto>> getRecipeListByUserId(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.getRecipeListByUserId(userId, page, size, principalUser));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiRespDto<RecipeListPageRespDto>> getMyRecipeList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.getMyRecipeList(page, size, principalUser));
    }
}

package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.recipe.AddRecipeReqDto;
import com.nullpoint.fifteenmintable.dto.recipe.ModifyRecipeReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board/{boardId}/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/add")
    public ResponseEntity<?> addRecipe(
            @PathVariable Integer boardId,
            @RequestBody AddRecipeReqDto addRecipeReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.addRecipe(boardId, addRecipeReqDto, principalUser));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getRecipeList(
            @PathVariable Integer boardId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(recipeService.getRecipeListByBoardId(boardId, page, size));
    }

    @GetMapping("/detail/{recipeId}")
    public ResponseEntity<?> getRecipeDetail(
            @PathVariable Integer boardId,
            @PathVariable Integer recipeId
    ) {
        return ResponseEntity.ok(recipeService.getRecipeDetail(boardId, recipeId));
    }

    @PutMapping("/modify/{recipeId}")
    public ResponseEntity<?> modifyRecipe(
            @PathVariable Integer boardId,
            @PathVariable Integer recipeId,
            @RequestBody ModifyRecipeReqDto modifyRecipeReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.modifyRecipe(boardId, recipeId, modifyRecipeReqDto, principalUser));
    }

    @DeleteMapping("/remove/{recipeId}")
    public ResponseEntity<?> removeRecipe(
            @PathVariable Integer boardId,
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.removeRecipe(boardId, recipeId, principalUser));
    }
}

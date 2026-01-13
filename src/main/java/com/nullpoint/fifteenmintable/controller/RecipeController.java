package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.recipe.AddRecipeReqDto;
import com.nullpoint.fifteenmintable.dto.recipe.ModifyRecipeReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping("/add")
    public ResponseEntity<?> addRecipe
            (@RequestBody AddRecipeReqDto addRecipeReqDto,
             @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(recipeService.addRecipe(addRecipeReqDto,principalUser));
    }

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(recipeService.getAll());
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeByRecipeId(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeByRecipeId(recipeId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getRecipeListByUsername(@PathVariable String username) {
        return ResponseEntity.ok(recipeService.getRecipeListByUsername(username));
    }

    @GetMapping("/category/main/{mainCategoryId}")
    public ResponseEntity<?> getRecipeListByMainCategoryId(@PathVariable Integer mainCategoryId) {
        return ResponseEntity.ok(recipeService.getRecipeListByMainCategoryId(mainCategoryId));
    }

    @GetMapping("/category/sub/{subCategoryId}")
    public ResponseEntity<?> getRecipeListBySubCategoryId(@PathVariable Integer subCategoryId) {
        return ResponseEntity.ok(recipeService.getRecipeListBySubCategoryId(subCategoryId));
    }

    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<?> getRecipeListByKeyword(@PathVariable String keyword) {
        return ResponseEntity.ok(recipeService.getRecipeListByKeyword(keyword));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modifyRecipe
            (@RequestBody ModifyRecipeReqDto modifyRecipeReqDto,
             @AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(recipeService.modifyRecipe(modifyRecipeReqDto, principalUser));
    }

    @PostMapping("/remove/{recipeId}")
    public ResponseEntity<?> removeRecipe(
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser) {
        return  ResponseEntity.ok(recipeService.removeRecipe(recipeId,principalUser));
    }
}

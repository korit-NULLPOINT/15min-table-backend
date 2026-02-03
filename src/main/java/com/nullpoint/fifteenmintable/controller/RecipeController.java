package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.*;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeService;
import com.nullpoint.fifteenmintable.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board/{boardId}/recipes")
@RequiredArgsConstructor
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    private final CommonUtil commonUtil;

    @PostMapping("/add")
    public ResponseEntity<ApiRespDto<Integer>> addRecipe(
            @PathVariable Integer boardId,
            @RequestBody AddRecipeReqDto addRecipeReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.addRecipe(boardId, addRecipeReqDto, principalUser));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiRespDto<RecipeListPageRespDto>> getRecipeList(
            @PathVariable Integer boardId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.getRecipeListByBoardId(boardId, page, size, principalUser));
    }

    @GetMapping("/list/filtered")
    public ResponseEntity<ApiRespDto<RecipeListPageRespDto>> getFilteredRecipeList(
            @PathVariable Integer boardId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @ModelAttribute RecipeFilterReqDto recipeFilterReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.getFilteredRecipeList(boardId, page, size, recipeFilterReqDto, principalUser));
    }


    @GetMapping("/detail/{recipeId}")
    public ResponseEntity<ApiRespDto<RecipeDetailRespDto>> getRecipeDetail(
            @PathVariable Integer boardId,
            @PathVariable Integer recipeId,
            HttpServletRequest request
    ) {
        String userIp = commonUtil.getClientIp(request);
        return ResponseEntity.ok(recipeService.getRecipeDetail(boardId, recipeId, userIp));
    }

    @PutMapping("/modify/{recipeId}")
    public ResponseEntity<ApiRespDto<Void>> modifyRecipe(
            @PathVariable Integer boardId,
            @PathVariable Integer recipeId,
            @RequestBody ModifyRecipeReqDto modifyRecipeReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.modifyRecipe(boardId, recipeId, modifyRecipeReqDto, principalUser));
    }

    @DeleteMapping("/remove/{recipeId}")
    public ResponseEntity<ApiRespDto<Void>> removeRecipe(
            @PathVariable Integer boardId,
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeService.removeRecipe(boardId, recipeId, principalUser));
    }
}

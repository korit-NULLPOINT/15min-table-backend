package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.hashtag.AddRecipeHashtagsReqDto;
import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeHashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipe-hashtag")
public class RecipeHashtagController {

    @Autowired
    private RecipeHashtagService recipeHashtagService;

    // 레시피 해시태그 저장(교체: 기존 삭제 후 재삽입) | hashtagNames: [] -> 빈 배열로 보내면 레시피에 연결 되어 있는 해시태그 전부 삭제
    @PostMapping("/add")
    public ResponseEntity<ApiRespDto<List<HashtagRespDto>>> addRecipeHashtags(
            @RequestBody AddRecipeHashtagsReqDto addRecipeHashtagsReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeHashtagService.saveRecipeHashtags(addRecipeHashtagsReqDto, principalUser));
    }

    // 레시피에 달린 해시태그 조회 (로그인 불필요)
    @GetMapping("/list/{recipeId}")
    public ResponseEntity<ApiRespDto<List<HashtagRespDto>>> getHashtagsByRecipeId(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeHashtagService.getHashtagsByRecipeId(recipeId));
    }

    // 해시태그 검색/자동완성 (로그인 불필요)
    @GetMapping("/search")
    public ResponseEntity<ApiRespDto<List<HashtagRespDto>>> searchHashtags(@RequestParam String keyword) {
        return ResponseEntity.ok(recipeHashtagService.searchHashtags(keyword));
    }
}

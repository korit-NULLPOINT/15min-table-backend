package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.*;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
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
        String userIp = this.getClientIp(request);
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

    // IP 가져오기 로직 - 재사용 필요시 util 폴더에 등록
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // ,(Comma) 분리 로직
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 여러 개일 경우 첫 번째 IP가 실제 클라이언트 IP입니다.
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            }
            return ip;
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}

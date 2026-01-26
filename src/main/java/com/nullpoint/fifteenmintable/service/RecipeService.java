package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.*;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.RecipeHashtagRepository;
import com.nullpoint.fifteenmintable.repository.RecipeRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeHashtagRepository recipeHashtagRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ApiRespDto<Integer> addRecipe(Integer boardId, AddRecipeReqDto addRecipeReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (addRecipeReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");

        if (addRecipeReqDto.getTitle() == null || addRecipeReqDto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("제목은 필수입니다.");
        }
        if (addRecipeReqDto.getSteps() == null || addRecipeReqDto.getSteps().trim().isEmpty()) {
            throw new BadRequestException("조리 과정은 필수입니다.");
        }

        Recipe recipe = addRecipeReqDto.toEntity(boardId);
        recipe.setUserId(principalUser.getUserId());
        recipe.setViewCount(0);

        int result = recipeRepository.addRecipe(recipe);
        if (result != 1) throw new RuntimeException("레시피 추가 실패");

        Integer recipeId = recipe.getRecipeId();

        // SSE 푸쉬
        notificationService.createRecipePostNotifications(recipe.getRecipeId(), principalUser);

        return new ApiRespDto<>("success", "레시피가 등록되었습니다.", recipeId);
    }

    public ApiRespDto<RecipeListPageRespDto> getRecipeListByBoardId(
            Integer boardId, Integer page, Integer size , PrincipalUser principalUser
    ) {
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");

        int safePage = (page == null) ? 0 : Math.max(page, 0);
        int safeSize = (size == null) ? 9 : Math.min(Math.max(size, 1), 50);
        int offset = safePage * safeSize;

        /*
        * page=0, size=20 → offset=0 → 0개 건너뛰고 20개 가져옴 (1~20번)
        * page=1, size=20 → offset=20 → 20개 건너뛰고 20개 가져옴 (21~40번)
        * page=2, size=20 → offset=40 → 41~60번
        * */

        Integer loginUserId = (principalUser == null) ? null : principalUser.getUserId();

        List<RecipeListRespDto> items =
                recipeRepository.getRecipeCardListByBoardId(boardId, loginUserId, offset, safeSize);

        int totalCount = recipeRepository.getRecipeCountByBoardId(boardId);

        RecipeListPageRespDto data = RecipeListPageRespDto.builder()
                .items(items)
                .totalCount(totalCount)
                .page(safePage)
                .size(safeSize)
                .build();

        return new ApiRespDto<>("success", "레시피 목록 조회 완료", data);
    }

    public ApiRespDto<RecipeListPageRespDto> getRecipeListByUserId(
            Integer userId, Integer page, Integer size, PrincipalUser principalUser
    ) {
        if (userId == null) throw new BadRequestException("userId는 필수입니다.");

        int safePage = (page == null) ? 0 : Math.max(page, 0);
        int safeSize = (size == null) ? 9 : Math.min(Math.max(size, 1), 50);
        int offset = safePage * safeSize;

        Integer loginUserId = (principalUser == null) ? null : principalUser.getUserId();

        List<RecipeListRespDto> items = recipeRepository.getRecipeCardListByUserId(userId, loginUserId, offset, safeSize);

        int totalCount = recipeRepository.getRecipeCountByUserId(userId);

        RecipeListPageRespDto data = RecipeListPageRespDto.builder()
                .items(items)
                .totalCount(totalCount)
                .page(safePage)
                .size(safeSize)
                .build();

        return new ApiRespDto<>("success", "유저 레시피 목록 조회 완료", data);
    }

    public ApiRespDto<RecipeListPageRespDto> getMyRecipeList(Integer page, Integer size, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");
        return getRecipeListByUserId(principalUser.getUserId(), page, size, principalUser);
    }


    @Transactional
    public ApiRespDto<RecipeDetailRespDto> getRecipeDetail(Integer boardId, Integer recipeId) {
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");

        recipeRepository.increaseViewCount(recipeId);

        RecipeDetailRespDto detail = recipeRepository.getRecipeDetail(boardId, recipeId)
                .orElseThrow(() -> new NotFoundException("해당 레시피가 존재하지 않습니다."));

        List<HashtagRespDto> hashtags = recipeHashtagRepository.getByRecipeId(recipeId);

        detail.setHashtags(hashtags);

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.", detail);
    }

    @Transactional
    public ApiRespDto<Void> modifyRecipe(Integer boardId, Integer recipeId, ModifyRecipeReqDto modifyRecipeReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");
        if (modifyRecipeReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");

        if (modifyRecipeReqDto.getTitle() == null || modifyRecipeReqDto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("제목은 필수입니다.");
        }
        if (modifyRecipeReqDto.getSteps() == null || modifyRecipeReqDto.getSteps().trim().isEmpty()) {
            throw new BadRequestException("조리 과정(steps)은 필수입니다.");
        }

        Recipe foundRecipe = recipeRepository.getRecipeEntityById(recipeId)
                .orElseThrow(() -> new NotFoundException("해당 레시피가 존재하지 않습니다."));

        if (!foundRecipe.getBoardId().equals(boardId)) {
            throw new NotFoundException("해당 게시판의 레시피가 아닙니다.");
        }

        boolean isOwner = foundRecipe.getUserId().equals(principalUser.getUserId());
        boolean isAdmin = principalUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        Recipe recipe = modifyRecipeReqDto.toEntity(recipeId);
        recipe.setUserId(principalUser.getUserId());

        int result = recipeRepository.modifyRecipe(recipe);
        if (result != 1) throw new RuntimeException("레시피 수정 실패");

        return new ApiRespDto<>("success", "레시피 수정에 성공했습니다.", null);
    }

    @Transactional
    public ApiRespDto<Void> removeRecipe(Integer boardId, Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");

        Recipe foundRecipe = recipeRepository.getRecipeEntityById(recipeId)
                .orElseThrow(() -> new NotFoundException("해당 레시피가 존재하지 않습니다."));

        if (!foundRecipe.getBoardId().equals(boardId)) {
            throw new NotFoundException("해당 게시판의 레시피가 아닙니다.");
        }

        boolean isOwner = foundRecipe.getUserId().equals(principalUser.getUserId());
        boolean isAdmin = principalUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        int result = recipeRepository.removeRecipe(recipeId, principalUser.getUserId());
        if (result != 1) throw new RuntimeException("레시피 삭제 실패");

        return new ApiRespDto<>("success", "레시피 삭제에 성공했습니다.", null);
    }
}
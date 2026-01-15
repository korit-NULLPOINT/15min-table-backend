package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.*;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.mapper.RecipeHashtagMapper;
import com.nullpoint.fifteenmintable.repository.RecipeRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeHashtagMapper recipeHashtagMapper;

    public ApiRespDto<?> addRecipe(Integer boardId, AddRecipeReqDto addRecipeReqDto, PrincipalUser principalUser) {
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

        return new ApiRespDto<>("success", "레시피가 등록되었습니다.", recipe.getRecipeId());
    }

    public ApiRespDto<?> getRecipeListByBoardId(Integer boardId, Integer page, Integer size) {
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");

        int safePage = (page == null) ? 0 : Math.max(page, 0);
        int safeSize = (size == null) ? 9 : Math.min(Math.max(size, 1), 50);
        int offset = safePage * safeSize;

        /*
        * page=0, size=20 → offset=0 → 0개 건너뛰고 20개 가져옴 (1~20번)
        * page=1, size=20 → offset=20 → 20개 건너뛰고 20개 가져옴 (21~40번)
        * page=2, size=20 → offset=40 → 41~60번
        * */

        List<RecipeListRespDto> items =
                recipeRepository.getRecipeCardListByBoardId(boardId, offset, safeSize);

        int totalCount = recipeRepository.getRecipeCountByBoardId(boardId);

        RecipeListPageRespDto data = RecipeListPageRespDto.builder()
                .items(items)
                .totalCount(totalCount)
                .page(safePage)
                .size(safeSize)
                .build();

        return new ApiRespDto<>("success", "레시피 목록 조회 완료", data);
    }

    @Transactional
    public ApiRespDto<?> getRecipeDetail(Integer boardId, Integer recipeId) {
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");

        recipeRepository.increaseViewCount(recipeId);

        RecipeDetailRespDto detail = recipeRepository.getRecipeDetail(boardId, recipeId)
                .orElseThrow(() -> new NotFoundException("해당 레시피가 존재하지 않습니다."));

        List<HashtagRespDto> hashtags = recipeHashtagMapper.getByRecipeId(recipeId);
        detail.setHashtags(hashtags);

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.", detail);
    }

    @Transactional
    public ApiRespDto<?> modifyRecipe(Integer boardId, Integer recipeId, ModifyRecipeReqDto modifyRecipeReqDto, PrincipalUser principalUser) {
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

        if (!foundRecipe.getUserId().equals(principalUser.getUserId())) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        Recipe recipe = modifyRecipeReqDto.toEntity(recipeId);
        recipe.setUserId(principalUser.getUserId());

        int result = recipeRepository.modifyRecipe(recipe);
        if (result != 1) throw new RuntimeException("레시피 수정 실패");

        return new ApiRespDto<>("success", "레시피 수정에 성공했습니다.", null);
    }

    @Transactional
    public ApiRespDto<?> removeRecipe(Integer boardId, Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");

        Recipe foundRecipe = recipeRepository.getRecipeEntityById(recipeId)
                .orElseThrow(() -> new NotFoundException("해당 레시피가 존재하지 않습니다."));

        if (!foundRecipe.getBoardId().equals(boardId)) {
            throw new NotFoundException("해당 게시판의 레시피가 아닙니다.");
        }

        boolean isOwner = foundRecipe.getUserId().equals(principalUser.getUserId());

        if (!isOwner) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        int result = recipeRepository.removeRecipe(recipeId, principalUser.getUserId());
        if (result != 1) throw new RuntimeException("레시피 삭제 실패");

        return new ApiRespDto<>("success", "레시피 삭제에 성공했습니다.", null);
    }


}
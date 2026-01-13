package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.recipe.AddRecipeReqDto;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.ModifyRecipeReqDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.repository.RecipeRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public ApiRespDto<?> addRecipe(AddRecipeReqDto addRecipeReqDto, PrincipalUser principalUser) {
        if(principalUser == null) {
            return new ApiRespDto<>("failed", "로그인 해주세요.", null);
        }

        Recipe recipe = addRecipeReqDto.toEntity();
        recipe.setUserId(principalUser.getUserId());
        recipe.setBoardId(1);

        int result = recipeRepository.createRecipe(recipe);

        if (result == 0) {
            return new ApiRespDto<>("failed", "레시피 추가에 실패했습니다.", null);
        }

        return new ApiRespDto<>("success","레시피가 등록되었습니다.", null);
    }

    public ApiRespDto<?> getAll() {
        return new ApiRespDto<>("success","레시피 전체 조회 완료", recipeRepository.findAll());
    }

    public ApiRespDto<?> getRecipeByRecipeId(Integer recipeId) {
        Optional<Recipe> foundRecipe = recipeRepository.findByRecipeId(recipeId);
        if(foundRecipe.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 레시피가 존재하지 않습니다.", null);
        }

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.",foundRecipe.get());
    }

    public ApiRespDto<?> getRecipeListByUsername(String username) {
        Optional<User> foundUserOptional = userRepository.getUserByUsername(username);

        if(foundUserOptional.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 username이 존재하지 않습니다.", null);
        }

        User foundUser = foundUserOptional.get();
        Optional<List<Recipe>> foundRecipeList
                = recipeRepository.findByUserId(foundUser.getUserId());

        if(foundRecipeList.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 유저가 등록한 레시피가 존재하지 않습니다.", null);
        }

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.",foundRecipeList.get());
    }

    public ApiRespDto<?> getRecipeListByMainCategoryId(Integer mainCategoryId) {
        Optional<List<Recipe>> foundRecipeList
                = recipeRepository.findByMainCategoryId(mainCategoryId);

        if(foundRecipeList.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 메인 카테고리의 레시피가 존재하지 않습니다.", null);
        }

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.",foundRecipeList.get());
    }

    public ApiRespDto<?> getRecipeListBySubCategoryId(Integer subCategoryId) {
        Optional<List<Recipe>> foundRecipeList
                = recipeRepository.findBySubCategoryId(subCategoryId);

        if(foundRecipeList.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 서브 카테고리의 레시피가 존재하지 않습니다.", null);
        }

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.",foundRecipeList.get());
    }

    public ApiRespDto<?> getRecipeListByKeyword(String keyword) {
        Optional<List<Recipe>> foundRecipeList
                =recipeRepository.findByKeyword(keyword);

        if(foundRecipeList.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 키워드의 레시피가 존재하지 않습니다.", null);
        }

        return new ApiRespDto<>("success", "게시물 조회에 성공했습니다.",foundRecipeList.get());
    }

    @Transactional
    public ApiRespDto<?> modifyRecipe(ModifyRecipeReqDto modifyRecipeReqDto, PrincipalUser principalUser) {
        if(principalUser == null) {
            return new ApiRespDto<>("failed", "로그인 해주세요.", null);
        }

        Optional<Recipe> foundRecipeOptional = recipeRepository.findByRecipeId(modifyRecipeReqDto.getRecipeId());

        if(foundRecipeOptional.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 레시피가 존재하지 않습니다.", null);
        }

        Recipe foundRecipe = foundRecipeOptional.get();
        
        if(!foundRecipe.getUserId().equals(principalUser.getUserId())) {
            return new ApiRespDto<>("failed", "권한이 없습니다.", null);
        }
        
        Recipe recipe = modifyRecipeReqDto.toEntity();

        System.out.println(">>>>> 레시피아이디 : " + recipe.getRecipeId());

        if (recipe.getMainCategoryId() == null) {
            recipe.setMainCategoryId(foundRecipe.getMainCategoryId());
        }

        if (recipe.getSubCategoryId() == null) {
            recipe.setSubCategoryId(foundRecipe.getSubCategoryId());
        }

        if (recipe.getTitle() == null) {
            recipe.setTitle(foundRecipe.getTitle());
        }

        if (recipe.getIntro() == null) {
            recipe.setIntro(foundRecipe.getIntro());
        }

        if (recipe.getThumbnailImgUrl() == null) {
            recipe.setThumbnailImgUrl(foundRecipe.getThumbnailImgUrl());
        }

        if (recipe.getIngredients() == null) {
            recipe.setIngredients(foundRecipe.getIngredients());
        }

        if (recipe.getIngredientImgUrl() == null) {
            recipe.setIngredientImgUrl(foundRecipe.getIngredientImgUrl());
        }

        if (recipe.getSteps() == null) {
            recipe.setSteps(foundRecipe.getSteps());
        }

        int result = recipeRepository.updateRecipe(recipe);

        if(result == 0) {
            return new ApiRespDto<>("failed", "레시피 수정에 실패했습니다.", null);
        }

        return new ApiRespDto<>("success", "레시피 수정에 성공했습니다.",null);
    }

    @Transactional
    public ApiRespDto<?> removeRecipe(Integer recipeId, PrincipalUser principalUser) {
        if(principalUser == null) {
            return new ApiRespDto<>("failed", "로그인 해주세요.", null);
        }

        Optional<Recipe> foundRecipe = recipeRepository.findByRecipeId(recipeId);

        if(foundRecipe.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 레시피가 존재하지 않습니다.", null);
        }

        boolean isOwner = foundRecipe.get().getUserId().equals(principalUser.getUserId());
        boolean isAdmin = principalUser
                .getUserRoles()
                .stream()
                .anyMatch(userRole -> userRole.getRoleId() == 1);

        if(!isOwner && !isAdmin) {
            return new ApiRespDto<>("failed", "권한이 없습니다.", null);
        }

        int result = recipeRepository.deleteRecipe(recipeId);

        if(result == 0) {
            return new ApiRespDto<>("failed", "레시피 삭제에 실패했습니다.", null);
        }

        return new ApiRespDto<>("success", "레시피 삭제에 성공했습니다.",null);
    }
}


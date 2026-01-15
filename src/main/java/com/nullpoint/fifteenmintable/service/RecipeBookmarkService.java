package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.bookmark.BookmarkRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeBookmark;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.RecipeBookmarkRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeBookmarkService {

    @Autowired
    private RecipeBookmarkRepository recipeBookmarkRepository;

    public ApiRespDto<?> addBookmark(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        Integer userId = principalUser.getUserId();


        boolean exists = recipeBookmarkRepository.existsByRecipeIdAndUserId(recipeId, userId);
        if (exists) {
            throw new RuntimeException("이미 찜한 레시피입니다.");
        }

        RecipeBookmark recipeBookmark = RecipeBookmark.builder()
                .recipeId(recipeId)
                .userId(userId)
                .build();

        int result = recipeBookmarkRepository.addBookmark(recipeBookmark);
        if (result != 1) {
            throw new RuntimeException("찜 추가 실패");
        }

        return new ApiRespDto<>("success", "찜 추가 완료", null);
    }

    public ApiRespDto<?> deleteBookmark(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        Integer userId = principalUser.getUserId();

        int result = recipeBookmarkRepository.deleteBookmark(recipeId, userId);
        if (result == 0) {
            throw new NotFoundException("삭제할 찜이 없습니다.");
        }

        return new ApiRespDto<>("success", "찜 삭제 완료", null);
    }

    public ApiRespDto<?> existsByRecipeId(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        boolean exists = recipeBookmarkRepository.existsByRecipeIdAndUserId(recipeId, principalUser.getUserId());
        return new ApiRespDto<>("success", "찜 여부 조회 완료", exists);
    }

    public ApiRespDto<?> getBookmarkListByUserId(PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        List<BookmarkRespDto> list = recipeBookmarkRepository.getBookmarkListByUserId(principalUser.getUserId());

        return new ApiRespDto<>("success", "찜 목록 조회 완료", list);
    }
}

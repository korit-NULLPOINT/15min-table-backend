package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.rating.RatingSummaryRespDto;
import com.nullpoint.fifteenmintable.dto.rating.RecipeRatingRespDto;
import com.nullpoint.fifteenmintable.dto.rating.UpsertRatingReqDto;
import com.nullpoint.fifteenmintable.entity.RecipeRating;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.RecipeRatingRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeRatingService {

    @Autowired
    private RecipeRatingRepository recipeRatingRepository;

    public ApiRespDto<RecipeRatingRespDto> upsertRating(UpsertRatingReqDto upsertRatingReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");

        if (upsertRatingReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");

        if (upsertRatingReqDto.getRecipeId() == null) throw new BadRequestException("recipeId는 필수입니다.");

        // 평점 범위 체크 (1~5)
        Integer rating = upsertRatingReqDto.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("평점은 1~5 사이여야 합니다.");
        }

        Integer userId = principalUser.getUserId();
        RecipeRating recipeRating = upsertRatingReqDto.toEntity(userId);

        boolean isRating = recipeRatingRepository
                .getRatingByRecipeIdAndUserId(recipeRating.getRecipeId(), recipeRating.getUserId())
                .isPresent();

        int result = isRating
                ? recipeRatingRepository.updateRating(recipeRating)
                : recipeRatingRepository.insertRating(recipeRating);

        if (result != 1) throw new RuntimeException("평점 등록/수정 실패");

        RecipeRatingRespDto saved = recipeRatingRepository.getRatingByRecipeIdAndUserId(upsertRatingReqDto.getRecipeId(), userId)
                .orElseThrow(() -> new RuntimeException("평점 저장 후 조회 실패"));

        return new ApiRespDto<>("success", "평점 등록/수정 완료", saved);
    }

    public ApiRespDto<Void> deleteRating(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        Integer userId = principalUser.getUserId();
        int result = recipeRatingRepository.deleteRating(recipeId, userId);

        if (result == 0) {
            throw new NotFoundException("삭제할 평점이 없습니다.");
        }

        return new ApiRespDto<>("success", "평점 삭제 완료", null);
    }

    public ApiRespDto<RecipeRatingRespDto> getRatingByRecipeIdAndUserId(Integer recipeId, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        if (recipeId == null) throw new BadRequestException("recipeId는 필수입니다.");

        RecipeRatingRespDto myRating = recipeRatingRepository
                .getRatingByRecipeIdAndUserId(recipeId, principalUser.getUserId())
                .orElse(null);

        return new ApiRespDto<>("success", "내 평점 조회 완료", myRating);
    }

    public ApiRespDto<RatingSummaryRespDto> getRatingSummary(Integer recipeId) {
        RatingSummaryRespDto summary = recipeRatingRepository
                .getRatingSummaryByRecipeId(recipeId)
                .orElseGet(() -> new RatingSummaryRespDto(0.0, 0)); // 평점 없을 때 기본값

        return new ApiRespDto<>("success", "평점 요약 조회 완료", summary);
    }
}

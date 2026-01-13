package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.rating.UpsertRatingReqDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RecipeRatingService recipeRatingService;

    @PostMapping("/upsert")
    public ResponseEntity<?> upsertRating(
            @RequestBody UpsertRatingReqDto upsertRatingReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeRatingService.upsertRating(upsertRatingReqDto, principalUser));
    }

    @PostMapping("/delete/{recipeId}")
    public ResponseEntity<?> deleteRating(
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeRatingService.deleteRating(recipeId, principalUser));
    }

    @GetMapping("/{recipeId}/get")
    public ResponseEntity<?> getRating(
            @PathVariable Integer recipeId,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(recipeRatingService.getRatingByRecipeIdAndUserId(recipeId, principalUser));
    }

    @GetMapping("/{recipeId}/summary")
    public ResponseEntity<?> getSummary(@PathVariable Integer recipeId) {
        return ResponseEntity.ok(recipeRatingService.getRatingSummary(recipeId));
    }
}

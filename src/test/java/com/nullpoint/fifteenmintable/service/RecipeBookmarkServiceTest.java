package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeBookmark;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.RecipeBookmarkRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeBookmarkServiceTest {

    @Mock
    private RecipeBookmarkRepository recipeBookmarkRepository;

    @InjectMocks
    private RecipeBookmarkService recipeBookmarkService;

    @Test
    void 찜_추가에_성공한다() {
        // given
        Integer recipeId = 10;
        Integer userId = 1;
        PrincipalUser principalUser = PrincipalUser.builder()
                .userId(userId)
                .build();

        when(recipeBookmarkRepository.existsByRecipeIdAndUserId(recipeId, userId))
                .thenReturn(false);

        when(recipeBookmarkRepository.addBookmark(any(RecipeBookmark.class)))
                .thenReturn(1);

        // when
        ApiRespDto<Void> response = recipeBookmarkService.addBookmark(recipeId, principalUser);

        // then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("찜 추가 완료");
        assertThat(response.getData()).isNull();

        ArgumentCaptor<RecipeBookmark> captor = ArgumentCaptor.forClass(RecipeBookmark.class);
        verify(recipeBookmarkRepository).addBookmark(captor.capture());

        RecipeBookmark savedBookmark = captor.getValue();
        assertThat(savedBookmark.getRecipeId()).isEqualTo(recipeId);
        assertThat(savedBookmark.getUserId()).isEqualTo(userId);
    }

    @Test
    void 이미_찜한_레시피면_예외가_발생한다() {
        // given
        Integer recipeId = 10;
        Integer userId = 1;
        PrincipalUser principalUser = PrincipalUser.builder()
                .userId(userId)
                .build();

        when(recipeBookmarkRepository.existsByRecipeIdAndUserId(recipeId, userId))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> recipeBookmarkService.addBookmark(recipeId, principalUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 찜한 레시피입니다.");

        verify(recipeBookmarkRepository, never()).addBookmark(any(RecipeBookmark.class));
    }

    @Test
    void 로그인하지_않으면_찜_추가에_실패한다() {
        // when & then
        assertThatThrownBy(() -> recipeBookmarkService.addBookmark(10, null))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("로그인이 필요합니다.");

        verifyNoInteractions(recipeBookmarkRepository);
    }
}
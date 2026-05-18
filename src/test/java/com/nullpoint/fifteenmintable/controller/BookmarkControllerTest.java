package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.RecipeBookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class BookmarkControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipeBookmarkService recipeBookmarkService;

    @InjectMocks
    private BookmarkController bookmarkController;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(bookmarkController).build();
    }

    @Test
    void 찜_여부_조회_API가_응답한다() throws Exception {
        // given
        Integer recipeId = 10;

        when(recipeBookmarkService.existsByRecipeId(
                eq(recipeId),
                nullable(PrincipalUser.class)
        )).thenReturn(new ApiRespDto<>("success", "찜 여부 조회 완료", true));

        // when & then
        mockMvc.perform(get("/bookmark/{recipeId}", recipeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("찜 여부 조회 완료"))
                .andExpect(jsonPath("$.data").value(true));

        verify(recipeBookmarkService).existsByRecipeId(
                eq(recipeId),
                nullable(PrincipalUser.class)
        );
    }
}
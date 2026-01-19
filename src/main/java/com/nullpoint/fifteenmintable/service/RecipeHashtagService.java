package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.hashtag.AddRecipeHashtagsReqDto;
import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import com.nullpoint.fifteenmintable.entity.Hashtag;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.entity.RecipeHashtag;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.HashtagRepository;
import com.nullpoint.fifteenmintable.repository.RecipeHashtagRepository;
import com.nullpoint.fifteenmintable.repository.RecipeRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecipeHashtagService {

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private RecipeHashtagRepository recipeHashtagRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * 레시피 해시태그 교체(전체 삭제 후 다시 삽입)
     */
    @Transactional
    public ApiRespDto<List<HashtagRespDto>> saveRecipeHashtags(AddRecipeHashtagsReqDto addRecipeHashtagsReqDto, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (addRecipeHashtagsReqDto == null) {
            throw new RuntimeException("요청 값이 비어있습니다.");
        }
        if (addRecipeHashtagsReqDto.getRecipeId() == null) {
            throw new RuntimeException("recipeId는 필수입니다.");
        }

        Integer recipeId = addRecipeHashtagsReqDto.getRecipeId();

        Recipe recipe = recipeRepository.getRecipeEntityById(recipeId)
                .orElseThrow(() -> new NotFoundException("레시피가 존재하지 않습니다."));

        boolean isOwner = recipe.getUserId().equals(principalUser.getUserId());
        boolean isAdmin = principalUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("해시태그 수정 권한이 없습니다.");
        }

        List<String> names = addRecipeHashtagsReqDto.getHashtagNames();

        // 1) 레시피에 달린 기존 연결 전부 삭제 (0이어도 정상)
        recipeHashtagRepository.deleteAllByRecipeId(recipeId);

        // 2) 입력 정리 + 중복 제거(UNIQUE 예외 방지)
        Set<String> cleaned = new LinkedHashSet<>();
        if (names != null) {
            for (String raw : names) {
                if (raw == null) continue;

                String n = raw.trim();
                if (n.isEmpty()) continue;

                if (n.startsWith("#")) n = n.substring(1).trim();
                if (n.isEmpty()) continue;

                // (선택) 너무 긴 태그 방지 (DB 컬럼 길이에 맞춰 조절)
                if (n.length() > 30) {
                    throw new RuntimeException("해시태그는 30자 이하여야 합니다.");
                }

                cleaned.add(n);
            }
        }

        // (선택) 태그 개수 제한 (원하면 숫자 바꿔)
        if (cleaned.size() > 4) {
            throw new RuntimeException("해시태그는 최대 4개까지 가능합니다.");
        }

        // 3) 해시태그 없으면 생성 -> recipe_hashtag 연결 생성
        for (String tagName : cleaned) {
            // ✅ tagName은 effectively final이라 람다 OK
            Hashtag hashtag = hashtagRepository.getByName(tagName)
                    .orElseGet(() -> {
                        Hashtag newTag = Hashtag.builder().name(tagName).build();
                        int result = hashtagRepository.createHashtag(newTag);
                        if (result != 1) throw new RuntimeException("해시태그 생성 실패");
                        return newTag;
                    });

            RecipeHashtag link = RecipeHashtag.builder()
                    .recipeId(recipeId)
                    .hashtagId(hashtag.getHashtagId())
                    .build();

            int result = recipeHashtagRepository.createRecipeHashtag(link);
            if (result != 1) throw new RuntimeException("레시피-해시태그 연결 생성 실패");
        }

        List<HashtagRespDto> hashtagRespDtos = recipeHashtagRepository.getByRecipeId(recipeId);
        return new ApiRespDto<>("success", "해시태그 저장 완료", hashtagRespDtos);
    }

    /**
     * 레시피에 달린 해시태그 조회 (로그인 불필요)
     */
    public ApiRespDto<List<HashtagRespDto>> getHashtagsByRecipeId(Integer recipeId) {
        if (recipeId == null) {
            throw new RuntimeException("recipeId는 필수입니다.");
        }

        List<HashtagRespDto> hashtagRespDtos = recipeHashtagRepository.getByRecipeId(recipeId);
        return new ApiRespDto<>("success", "해시태그 조회 완료", hashtagRespDtos);
    }

    /**
     * 해시태그 검색(자동완성) (로그인 불필요)
     */
    public ApiRespDto<List<HashtagRespDto>> searchHashtags(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ApiRespDto<>("success", "검색 결과", List.of());
        }

        String k = keyword.trim();
        List<Hashtag> list = hashtagRepository.getByKeyword(k);

        List<HashtagRespDto> resp = new ArrayList<>();
        for (Hashtag h : list) {
            resp.add(new HashtagRespDto(h.getHashtagId(), h.getName()));
        }

        return new ApiRespDto<>("success", "검색 완료", resp);
    }
}

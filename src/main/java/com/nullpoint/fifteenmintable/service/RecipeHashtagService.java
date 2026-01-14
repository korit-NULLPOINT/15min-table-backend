package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.hashtag.AddRecipeHashtagsReqDto;
import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import com.nullpoint.fifteenmintable.entity.Hashtag;
import com.nullpoint.fifteenmintable.entity.RecipeHashtag;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.HashtagRepository;
import com.nullpoint.fifteenmintable.repository.RecipeHashtagRepository;
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

    /**
     * 레시피 해시태그 교체(전체 삭제 후 다시 삽입)
     */
    @Transactional
    public ApiRespDto<?> saveRecipeHashtags(AddRecipeHashtagsReqDto dto, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        if (dto == null) {
            throw new RuntimeException("요청 값이 비어있습니다.");
        }
        if (dto.getRecipeId() == null) {
            throw new RuntimeException("recipeId는 필수입니다.");
        }

        Integer recipeId = dto.getRecipeId();
        List<String> names = dto.getHashtagNames();

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
                        int r = hashtagRepository.createHashtag(newTag);
                        if (r != 1) throw new RuntimeException("해시태그 생성 실패");
                        return newTag;
                    });

            RecipeHashtag link = RecipeHashtag.builder()
                    .recipeId(recipeId)
                    .hashtagId(hashtag.getHashtagId())
                    .build();

            int r = recipeHashtagRepository.createRecipeHashtag(link);
            if (r != 1) throw new RuntimeException("레시피-해시태그 연결 생성 실패");
        }

        // 4) 저장 후 최종 목록 반환(조인해서 hashtag까지 같이 내려옴)
        List<RecipeHashtag> saved = recipeHashtagRepository.getByRecipeId(recipeId);

        List<HashtagRespDto> resp = new ArrayList<>();
        for (RecipeHashtag rh : saved) {
            if (rh.getHashtag() != null) {
                resp.add(new HashtagRespDto(
                        rh.getHashtag().getHashtagId(),
                        rh.getHashtag().getName()
                ));
            }
        }

        return new ApiRespDto<>("success", "해시태그 저장 완료", resp);
    }

    /**
     * 레시피에 달린 해시태그 조회 (로그인 불필요)
     */
    public ApiRespDto<?> getHashtagsByRecipeId(Integer recipeId) {
        if (recipeId == null) {
            throw new RuntimeException("recipeId는 필수입니다.");
        }

        List<RecipeHashtag> list = recipeHashtagRepository.getByRecipeId(recipeId);

        // 해시태그가 없는 건 404로 볼지, 빈 배열로 볼지 정책 문제인데
        // 일반적으로는 "빈 배열"이 더 자연스러움.
        List<HashtagRespDto> resp = new ArrayList<>();
        for (RecipeHashtag rh : list) {
            if (rh.getHashtag() != null) {
                resp.add(new HashtagRespDto(
                        rh.getHashtag().getHashtagId(),
                        rh.getHashtag().getName()
                ));
            }
        }

        return new ApiRespDto<>("success", "해시태그 조회 완료", resp);
    }

    /**
     * 해시태그 검색(자동완성) (로그인 불필요)
     */
    public ApiRespDto<?> searchHashtags(String keyword) {
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

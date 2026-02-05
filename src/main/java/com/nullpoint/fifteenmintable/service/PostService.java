package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.CursorRespDto;
import com.nullpoint.fifteenmintable.dto.post.AddPostReqDto;
import com.nullpoint.fifteenmintable.dto.post.ModifyPostReqDto;
import com.nullpoint.fifteenmintable.dto.post.PostDetailRespDto;
import com.nullpoint.fifteenmintable.dto.post.PostListRespDto;
import com.nullpoint.fifteenmintable.entity.Post;
import com.nullpoint.fifteenmintable.entity.PostImg;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.repository.CommentRepository;
import com.nullpoint.fifteenmintable.repository.PostRepository;
import com.nullpoint.fifteenmintable.repository.PostImgRepository;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImgRepository postImgRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final StringRedisTemplate redisTemplate;

    @Transactional
    public ApiRespDto<Integer> addPost(Integer boardId, AddPostReqDto addPostReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (addPostReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");

        if (addPostReqDto.getTitle() == null || addPostReqDto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("제목은 필수입니다.");
        }
        if (addPostReqDto.getContent() == null || addPostReqDto.getContent().trim().isEmpty()) {
            throw new BadRequestException("내용은 필수입니다.");
        }

        Post post = addPostReqDto.toEntity(boardId, principalUser.getUserId());
        post.setViewCount(0);

        int result = postRepository.addPost(post);
        if (result != 1) throw new RuntimeException("게시글 등록 실패");

        Integer postId = post.getPostId();

        List<String> imgUrls = addPostReqDto.getImgUrls();
        if (imgUrls != null) {
            List<PostImg> postImgs = buildPostImgs(postId, imgUrls);
            if (!postImgs.isEmpty()) {
                int imgResult = postImgRepository.addPostImgs(postImgs);
                if (imgResult < 1) throw new RuntimeException("게시글 이미지 등록 실패");
            }
        }

        return new ApiRespDto<>("success", "게시글이 등록되었습니다.", postId);
    }

    @Transactional
    public ApiRespDto<PostDetailRespDto> getPostDetail(Integer boardId, Integer postId, String userIp) {
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (postId == null) throw new BadRequestException("postId는 필수입니다.");

        String key = "view_log:" + boardId + ":" + postId + ":" + userIp;

        try {
            // 1. Redis 확인
            if (redisTemplate.opsForValue().get(key) == null) {
                postRepository.increaseViewCount(postId);
                redisTemplate.opsForValue().set(key, "1", Duration.ofHours(24));
            }
        } catch (Exception e) {
            // 2. Redis 연결 실패 시 -> 로컬 캐시로 Fallback
            log.warn("Redis 연결 실패! 로컬 캐시로 전환합니다. ({})", e.getMessage());

            // 로컬 캐시에 없으면 카운트 증가 - caffeine 의존성 제거 완료
//            if (localFallbackCache.getIfPresent(key) == null) {
//                recipeRepository.increaseViewCount(recipeId); // DB 증가
//                localFallbackCache.put(key, true); // 로컬에 기록
//            }
        }

        PostDetailRespDto detail = postRepository.getPostDetail(boardId, postId)
                .orElseThrow(() -> new NotFoundException("해당 게시글이 존재하지 않습니다."));

        List<String> imgUrls = postImgRepository.getImgUrlsByPostId(postId);
        detail.setImgUrls(imgUrls);

        return new ApiRespDto<>("success", "게시글 조회에 성공했습니다.", detail);
    }

    @Transactional
    public ApiRespDto<Void> modifyPost(Integer boardId, Integer postId, ModifyPostReqDto modifyPostReqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (postId == null) throw new BadRequestException("postId는 필수입니다.");
        if (modifyPostReqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");

        if (modifyPostReqDto.getTitle() == null || modifyPostReqDto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("제목은 필수입니다.");
        }
        if (modifyPostReqDto.getContent() == null || modifyPostReqDto.getContent().trim().isEmpty()) {
            throw new BadRequestException("내용은 필수입니다.");
        }

        Post foundPost = postRepository.getPostById(postId)
                .orElseThrow(() -> new NotFoundException("해당 게시글이 존재하지 않습니다."));

        if (!foundPost.getBoardId().equals(boardId)) {
            throw new NotFoundException("해당 게시판의 게시글이 아닙니다.");
        }

        boolean isOwner = foundPost.getUserId().equals(principalUser.getUserId());
        boolean isAdmin = principalUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        int result = postRepository.modifyPost(postId, modifyPostReqDto.getTitle(), modifyPostReqDto.getContent());
        if (result != 1) throw new RuntimeException("게시글 수정 실패");

        if (modifyPostReqDto.hasImgUrlsField()) {
            postImgRepository.deleteByPostId(postId);

            List<PostImg> postImgs = buildPostImgs(postId, modifyPostReqDto.getImgUrls());
            if (!postImgs.isEmpty()) {
                int imgResult = postImgRepository.addPostImgs(postImgs);
                if (imgResult < 1) throw new RuntimeException("게시글 이미지 수정 실패");
            }
        }

        return new ApiRespDto<>("success", "게시글 수정에 성공했습니다.", null);
    }

    @Transactional
    public ApiRespDto<Void> deletePost(Integer boardId, Integer postId, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");
        if (postId == null) throw new BadRequestException("postId는 필수입니다.");

        Post foundPost = postRepository.getPostById(postId)
                .orElseThrow(() -> new NotFoundException("해당 게시글이 존재하지 않습니다."));

        if (!foundPost.getBoardId().equals(boardId)) {
            throw new NotFoundException("해당 게시판의 게시글이 아닙니다.");
        }

        boolean isOwner = foundPost.getUserId().equals(principalUser.getUserId());
        boolean isAdmin = principalUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        commentRepository.deleteByTarget("POST", postId);

        postImgRepository.deleteByPostId(postId);

        int result = postRepository.deletePost(postId);
        if (result != 1) throw new RuntimeException("게시글 삭제 실패");

        return new ApiRespDto<>("success", "게시글 삭제에 성공했습니다.", null);
    }

    public ApiRespDto<CursorRespDto<PostListRespDto>> getPostListByCursor(
            Integer boardId,
            Integer size,
            String cursor,
            String keyword,
            String searchType
    ) {
        if (boardId == null) throw new BadRequestException("boardId는 필수입니다.");

        int safeSize = (size == null || size <= 0) ? 20 : Math.min(size, 50);
        int sizePlusOne = safeSize + 1;

        String safeSearchType = (searchType == null || searchType.isBlank()) ? null : searchType.trim();
        if (safeSearchType != null) {
            if (!safeSearchType.equals("TITLE") && !safeSearchType.equals("USERNAME") && !safeSearchType.equals("ALL")) {
                safeSearchType = null;
            }
            if (safeSearchType.equals("ALL")) safeSearchType = null;
        }

        CursorValue cursorValue = parseCursor(cursor);

        List<PostListRespDto> rows = postRepository.getPostListByCursor(
                boardId,
                sizePlusOne,
                cursorValue.cursorCreateDt,
                cursorValue.cursorPostId,
                keyword,
                safeSearchType
        );

        boolean hasNext = rows.size() > safeSize;
        List<PostListRespDto> items = hasNext ? rows.subList(0, safeSize) : rows;

        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            PostListRespDto last = items.get(items.size() - 1);
            nextCursor = last.getCreateDt() + "|" + last.getPostId();
        }

        CursorRespDto<PostListRespDto> data = new CursorRespDto<>(items, hasNext, nextCursor);
        return new ApiRespDto<>("success", "게시글 목록 조회 완료", data);
    }

    private List<PostImg> buildPostImgs(Integer postId, List<String> imgUrls) {
        List<PostImg> postImgs = new ArrayList<>();
        if (imgUrls == null) return postImgs;

        int order = 0;
        for (String url : imgUrls) {
            if (url == null || url.trim().isEmpty()) continue;

            postImgs.add(PostImg.builder()
                    .postId(postId)
                    .imgUrl(url.trim())
                    .sortOrder(order++)
                    .build());
        }
        return postImgs;
    }

    @Transactional
    public ApiRespDto<CursorRespDto<PostListRespDto>> getPostListByUserIdByCursor(
            Integer userId,
            Integer size,
            String cursor
    ) {
        if (userId == null) throw new BadRequestException("userId는 필수입니다.");

        int safeSize = (size == null || size <= 0) ? 20 : Math.min(size, 50);
        int sizePlusOne = safeSize + 1;

        CursorValue cv = parseCursor(cursor);

        List<PostListRespDto> rows = postRepository.getPostListByUserIdByCursor(
                userId,
                sizePlusOne,
                cv.cursorCreateDt,
                cv.cursorPostId
        );

        boolean hasNext = rows.size() > safeSize;
        List<PostListRespDto> items = hasNext ? rows.subList(0, safeSize) : rows;

        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            PostListRespDto last = items.get(items.size() - 1);
            nextCursor = last.getCreateDt() + "|" + last.getPostId();
        }

        CursorRespDto<PostListRespDto> data = new CursorRespDto<>(items, hasNext, nextCursor);
        return new ApiRespDto<>("success", "유저 게시글 목록 조회 완료", data);
    }

    @Transactional
    public ApiRespDto<CursorRespDto<PostListRespDto>> getMyPostListByCursor(
            Integer size,
            String cursor,
            PrincipalUser principalUser
    ) {
        if (principalUser == null) throw new UnauthenticatedException("로그인이 필요합니다.");
        return getPostListByUserIdByCursor(principalUser.getUserId(), size, cursor);
    }


    // cursor(String: "createDt|postId")를 DB 비교용 값(LocalDateTime, Integer)으로 파싱/검증해서 반환
    private CursorValue parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            // 커서가 없으면 첫 페이지 조회(커서 조건 없이 조회)
            return new CursorValue(null, null);
        }

        try {
            String[] parts = cursor.split("\\|");
            if (parts.length != 2) throw new IllegalArgumentException();

            LocalDateTime cursorCreateDt = LocalDateTime.parse(parts[0]);
            Integer cursorPostId = Integer.parseInt(parts[1]);

            return new CursorValue(cursorCreateDt, cursorPostId);
        } catch (Exception e) {
            // cursor 형식 오류 → 400 BadRequest
            throw new BadRequestException("cursor 형식이 올바르지 않습니다. 예) 2026-01-30T00:12:33|123");
        }
    }

    // 커서 파싱 결과(createDt, postId) 2개 값을 묶어 전달하기 위한 서비스 내부 전용 컨테이너
    private static class CursorValue {
        private final LocalDateTime cursorCreateDt;
        private final Integer cursorPostId;

        private CursorValue(LocalDateTime cursorCreateDt, Integer cursorPostId) {
            this.cursorCreateDt = cursorCreateDt;
            this.cursorPostId = cursorPostId;
        }
    }

}

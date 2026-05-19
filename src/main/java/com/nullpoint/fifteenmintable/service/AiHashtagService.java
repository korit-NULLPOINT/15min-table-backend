package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.ai.AiHashtagReqDto;
import com.nullpoint.fifteenmintable.dto.ai.AiHashtagRespDto;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiHashtagService {

    private static final int DEFAULT_LIMIT = 4;
    private static final int MIN_LIMIT = 3;
    private static final int MAX_LIMIT = 4;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.agent.url}")
    private String agentUrl;

    public ApiRespDto<AiHashtagRespDto> generateRecipeHashtags(AiHashtagReqDto reqDto, PrincipalUser principalUser) {
        if (principalUser == null) {
            throw new UnauthenticatedException("로그인 해주세요.");
        }

        if (reqDto == null) {
            throw new BadRequestException("요청 값이 비어있습니다.");
        }

        String title = safe(reqDto.getTitle());
        String steps = safe(reqDto.getSteps());

        if (title.isEmpty()) {
            throw new BadRequestException("title(제목)은 필수입니다.");
        }

        if (steps.isEmpty()) {
            throw new BadRequestException("steps(조리순서)는 필수입니다.");
        }

        int limit = normalizeLimit(reqDto.getLimit());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("intro", safe(reqDto.getIntro()));
        requestBody.put("ingredients", safe(reqDto.getIngredients()));
        requestBody.put("steps", steps);
        requestBody.put("limit", limit);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<AgentHashtagRespDto> response;

        try {
            response = restTemplate.exchange(
                    agentUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    AgentHashtagRespDto.class
            );
        } catch (RestClientException e) {
            log.error("LangGraph Agent 서버 호출 에러", e);
            throw new RuntimeException("AI Agent 서비스 호출 중 오류가 발생했습니다.", e);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("AI Agent 서비스 호출 실패: " + response.getStatusCode());
        }

        List<String> hashtags = response.getBody().hashtags();

        if (hashtags == null) {
            hashtags = List.of();
        }

        return new ApiRespDto<>("success", "AI 해시태그 생성 완료", new AiHashtagRespDto(hashtags));
    }

    private record AgentHashtagRespDto(List<String> hashtags) {
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) return DEFAULT_LIMIT;
        if (limit <= MIN_LIMIT) return MIN_LIMIT;
        return Math.min(limit, MAX_LIMIT);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
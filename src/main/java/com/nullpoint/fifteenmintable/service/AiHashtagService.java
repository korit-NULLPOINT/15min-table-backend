package com.nullpoint.fifteenmintable.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.ai.AiHashtagReqDto;
import com.nullpoint.fifteenmintable.dto.ai.AiHashtagRespDto;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiHashtagService {

    // ===== 정책값(원하면 여기만 조정) =====
    private static final int DEFAULT_LIMIT = 4;
    private static final int MIN_LIMIT = 3;
    private static final int MAX_LIMIT = 4;

    private static final int MIN_TAG_LEN = 2;          // '#' 제외 최소 길이
    private static final int MAX_TAG_LEN = 15;         // '#' 제외 최대 길이 (요청하신 10자)

    // 너무 일반적인 태그(검색 품질 떨어짐) - 원하면 계속 추가
    private static final Set<String> BANNED_TAGS = Set.of(
            "요리", "레시피", "음식", "맛집", "먹방", "간식", "집밥"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    public ApiRespDto<AiHashtagRespDto> generateRecipeHashtags(AiHashtagReqDto reqDto, PrincipalUser principalUser) {
        if (principalUser == null) throw new UnauthenticatedException("로그인 해주세요.");
        if (reqDto == null) throw new BadRequestException("요청 값이 비어있습니다.");

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("GEMINI_API_KEY가 설정되지 않았습니다.");
        }

        String title = safe(reqDto.getTitle());
        String steps = safe(reqDto.getSteps());

        if (title.isEmpty()) throw new BadRequestException("title(제목)은 필수입니다.");
        if (steps.isEmpty()) throw new BadRequestException("steps(조리순서)는 필수입니다.");

        int limit = normalizeLimit(reqDto.getLimit()); // 3~4 강제

        String userPrompt = buildUserPrompt(reqDto);
        String systemInstruction = buildSystemInstruction(limit);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("contents", List.of(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userPrompt))
        )));
        requestBody.put("systemInstruction", Map.of(
                "role", "system",
                "parts", List.of(Map.of("text", systemInstruction))
        ));
        requestBody.put("generationConfig", Map.of(
                "responseMimeType", "application/json",
                "responseJsonSchema", buildHashtagSchema(limit)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        String url = baseUrl + "/models/" + model + ":generateContent";

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Gemini 호출 중 네트워크/요청 오류가 발생했습니다.", e);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Gemini 호출 실패: " + response.getStatusCode());
        }

        List<String> rawHashtags = parseHashtagsFromGeminiResponse(response.getBody(), limit);

        // ✅ 후처리(정규화/필터/길이강제/중복제거)
        List<String> hashtags = postProcess(rawHashtags, limit);

        return new ApiRespDto<>("success", "AI 해시태그 생성 완료", new AiHashtagRespDto(hashtags));
    }

    private Map<String, Object> buildHashtagSchema(int limit) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", Map.of(
                "hashtags", Map.of(
                        "type", "array",
                        "items", Map.of("type", "string"),
                        "minItems", Math.min(MIN_LIMIT, limit),
                        "maxItems", limit
                )
        ));
        schema.put("required", List.of("hashtags"));
        schema.put("additionalProperties", false);
        return schema;
    }

    private String buildSystemInstruction(int limit) {
        return """
                너는 한국어 요리 레시피 해시태그 생성기야.
                반드시 JSON만 출력해. (설명/문장/코드블록 금지)
                출력 형식: {"hashtags":["#...","#..."]}

                규칙:
                - 해시태그는 최대 %d개 (가능하면 %d개)
                - 각 해시태그는 '#' 제외 2~10자 이내
                - 재료/조리법/상황(자취, 초간단, 다이어트 등)을 반영
                - 중복/유사어는 피하고, 특수문자/띄어쓰기 최소화
                - 너무 일반적인 단어(요리/레시피/음식)는 피하기
                """.formatted(limit, limit);
    }

    private String buildUserPrompt(AiHashtagReqDto dto) {
        return """
                아래 레시피 정보를 보고 해시태그를 추천해줘.

                [제목]
                %s

                [소개]
                %s

                [재료]
                %s

                [조리순서]
                %s
                """.formatted(
                safe(dto.getTitle()),
                safe(dto.getIntro()),
                safe(dto.getIngredients()),
                safe(dto.getSteps())
        );
    }

    private List<String> parseHashtagsFromGeminiResponse(String rawJson, int limit) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                return List.of();
            }

            String modelText = textNode.asText().trim();

            JsonNode structured = objectMapper.readTree(modelText);
            JsonNode arr = structured.path("hashtags");
            if (!arr.isArray()) return List.of();

            List<String> result = new ArrayList<>();
            for (JsonNode n : arr) {
                if (n.isTextual()) result.add(n.asText());
            }
            return result.stream().limit(limit).collect(Collectors.toList());

        } catch (Exception e) {
            // JSON 깨진 경우 → 안전하게 빈 배열
            return List.of();
        }
    }

    /**
     * 후처리 규칙:
     * - 공백/따옴표 제거
     * - '#' 강제
     * - 허용 문자만 유지(한글/영문/숫자)
     * - 길이: '#' 제외 2~10자
     * - 너무 일반 태그(BANNED_TAGS) 제거
     * - 중복 제거 후 limit만큼 반환
     */
    private List<String> postProcess(List<String> hashtags, int limit) {
        if (hashtags == null || hashtags.isEmpty()) return List.of();

        return hashtags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.replaceAll("\\s+", ""))      // 공백 제거
                .map(s -> s.replaceAll("[\"'`]", ""))    // 따옴표류 제거
                .map(this::ensureHashPrefix)             // # 강제
                .map(this::sanitizeBody)                 // 허용 문자만 남기기
                .map(this::trimToMaxLen)                 // 10자 이내 강제
                .filter(this::isValidLen)                // 최소 길이(2자) 필터
                .filter(this::isNotBanned)               // 금칙어 필터
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private String ensureHashPrefix(String s) {
        return s.startsWith("#") ? s : "#" + s;
    }

    /**
     * '#' 제외한 본문에서 한글/영문/숫자만 남김.
     * 예: "#전자레인지-요리!" -> "#전자레인지요리"
     */
    private String sanitizeBody(String tag) {
        String body = tag.startsWith("#") ? tag.substring(1) : tag;
        body = body.replaceAll("[^0-9A-Za-z가-힣]", "");
        return "#" + body;
    }

    /**
     * '#' 제외 본문 10자 이내로 강제(넘치면 자름)
     */
    private String trimToMaxLen(String tag) {
        String body = tag.startsWith("#") ? tag.substring(1) : tag;
        if (body.length() <= MAX_TAG_LEN) return "#" + body;
        return "#" + body.substring(0, MAX_TAG_LEN);
    }

    private boolean isValidLen(String tag) {
        String body = tag.startsWith("#") ? tag.substring(1) : tag;
        return body.length() >= MIN_TAG_LEN && body.length() <= MAX_TAG_LEN;
    }

    private boolean isNotBanned(String tag) {
        String body = tag.startsWith("#") ? tag.substring(1) : tag;
        return !BANNED_TAGS.contains(body);
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

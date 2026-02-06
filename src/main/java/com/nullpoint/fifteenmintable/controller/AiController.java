package com.nullpoint.fifteenmintable.controller;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.ai.AiHashtagReqDto;
import com.nullpoint.fifteenmintable.dto.ai.AiHashtagRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.AiHashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiHashtagService aiHashtagService;

    @PostMapping("/hashtags")
    public ResponseEntity<ApiRespDto<AiHashtagRespDto>> generateHashtags(
            @RequestBody AiHashtagReqDto reqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
    ) {
        return ResponseEntity.ok(aiHashtagService.generateRecipeHashtags(reqDto, principalUser));
    }
}

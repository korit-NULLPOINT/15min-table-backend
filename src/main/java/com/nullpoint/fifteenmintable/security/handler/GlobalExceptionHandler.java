package com.nullpoint.fifteenmintable.security.handler;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.ok(new ApiRespDto<>("failed", "문제가 발생했습니다. :" + e.getMessage(), null));
    }
}

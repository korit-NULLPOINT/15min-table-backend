package com.nullpoint.fifteenmintable.exception.handler;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.exception.ForbiddenException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.exception.UnauthenticatedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiRespDto<?>> handleUnauthenticated(UnauthenticatedException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(new ApiRespDto<>("failed", e.getMessage(), null));
    }

//    if (principalUser == null) {
//        throw new UnauthenticatedException("로그인이 필요합니다.");
//    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiRespDto<?>> handleForbidden(ForbiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(new ApiRespDto<>("failed", e.getMessage(), null));
    }

//    if (!comment.getUserId().equals(principalUser.getUserId())) {
//        throw new ForbiddenException("권한이 없습니다.");
//    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiRespDto<?>> handleNotFound(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(new ApiRespDto<>("failed", e.getMessage(), null));
    }

//    Comment comment = commentRepository.getById(commentId)
//            .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiRespDto<?>> handleDataAccess(DataAccessException e) {
        // 운영에서는 e.getMessage() 노출 비추
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new ApiRespDto<>("failed", "DB 오류가 발생했습니다.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiRespDto<?>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new ApiRespDto<>("failed", "서버 오류가 발생했습니다.", null));
    }
}
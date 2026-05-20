package com.sbproject.weaver.common.exception;


import com.sbproject.weaver.common.dto.ApiResult;
import com.sbproject.weaver.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - 데이터 없음
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 400 - 잘못된 요청 (파라미터 오류, 파트 누락, 타입 불일치)
    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.BAD_REQUEST.value(),
                e.getClass().getSimpleName(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 409 - 중복 데이터 (username, email unique 제약 위반)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                "이미 존재하는 데이터입니다."
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 413 - 파일 크기 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUpload(MaxUploadSizeExceededException e) {
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "PAYLOAD_TOO_LARGE",
                "업로드 가능한 파일 크기를 초과했습니다."
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    // 500 - 파일 I/O 오류 (FileStorage 저장/삭제 실패)
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIO(IOException e) {
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "IO_ERROR",
                "파일 처리 중 오류가 발생했습니다: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // 500 - RuntimeException (FileStorage RuntimeException 포함)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException e, HttpServletRequest req) {
        e.printStackTrace();
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "RUNTIME_ERROR",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // 500 - 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleEtc(Exception e, HttpServletRequest req) {
        e.printStackTrace();
        ErrorResponse body = ErrorResponse.fail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
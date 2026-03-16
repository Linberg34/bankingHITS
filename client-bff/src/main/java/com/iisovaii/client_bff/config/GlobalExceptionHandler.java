package com.iisovaii.client_bff.config;

import com.iisovaii.client_bff.dto.error.ErrorResponse;
import com.iisovaii.client_bff.security.JwtExpiredException;
import com.iisovaii.client_bff.security.JwtInvalidException;
import com.iisovaii.client_bff.security.UnauthorizedException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ErrorResponse> handleExpired(JwtExpiredException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error("TOKEN_EXPIRED", e.getMessage()));
    }

    @ExceptionHandler(JwtInvalidException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(JwtInvalidException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error("TOKEN_INVALID", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error("UNAUTHORIZED", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error("FORBIDDEN", "Нет доступа к ресурсу"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error("VALIDATION_ERROR", message));
    }

    // ошибки от Feign клиентов (AccountService вернул 400/403/404)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException e) {
        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(error("UPSTREAM_ERROR", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("INTERNAL_ERROR", "Внутренняя ошибка сервера"));
    }

    private ErrorResponse error(String code, String message) {
        return new ErrorResponse(code, message, LocalDateTime.now());
    }
}

package com.monst.handler;

import java.util.List;

import com.monst.dto.response.ErrorResponse;
import com.monst.dto.response.ErrorResponse.FieldError;
import com.monst.service.exception.EmailAlreadyUsedException;
import com.monst.service.exception.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 400: Validation ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        List<FieldError> details = result.getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ---------- 401: Unauthorized ----------
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse body = new ErrorResponse(
                "UNAUTHORIZED",
                ex.getMessage() != null ? ex.getMessage() : "Unauthorized",
                List.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ---------- 409: Conflict (email already used) ----------
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        ErrorResponse body = new ErrorResponse(
                "EMAIL_ALREADY_USED",
                ex.getMessage() != null ? ex.getMessage() : "Email already used",
                List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ---------- 500: Fallback ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        // ログは本来 logger で出す（ここでは簡略化）
        ErrorResponse body = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected error occurred",
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

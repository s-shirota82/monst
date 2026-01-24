package com.monst.handler;

import java.util.List;

import com.monst.dto.response.ErrorResponse;
import com.monst.dto.response.ErrorResponse.FieldError;
import com.monst.exception.EmailAlreadyUsedException;
import com.monst.exception.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 400: Validation (Bean Validation) ----------
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

    // ---------- 400/xxx: ResponseStatusException ----------
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        // /monster/register の手動バリデーションはここ（400）
        if (status == HttpStatus.BAD_REQUEST) {
            ErrorResponse body = new ErrorResponse(
                    "VALIDATION_ERROR",
                    ex.getReason() != null ? ex.getReason() : "Validation failed",
                    List.of());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        // それ以外の ResponseStatusException も一応整形
        ErrorResponse body = new ErrorResponse(
                "ERROR",
                ex.getReason() != null ? ex.getReason() : "Request failed",
                List.of());
        return ResponseEntity.status(status).body(body);
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
        ErrorResponse body = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected error occurred",
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

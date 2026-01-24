package com.monst.dto.response;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        List<FieldError> details) {
    public record FieldError(
            String field,
            String message) {
    }
}

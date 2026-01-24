package com.monst.dto.response;

public record RegisterResponse(
        long id,
        String email,
        String name) {
}

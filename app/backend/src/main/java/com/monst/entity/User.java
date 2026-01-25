package com.monst.entity;

public record User(
        Long id,
        String email,
        String password,
        String name,
        UserRole role) {
}

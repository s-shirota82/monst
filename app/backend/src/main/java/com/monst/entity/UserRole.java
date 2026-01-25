package com.monst.entity;

public enum UserRole {
    USER(0),
    ADMIN(1);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole fromValue(int value) {
        for (UserRole r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }
}

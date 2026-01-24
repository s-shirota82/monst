package com.monst.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "email is required") @Email(message = "email must be a valid email address") String email,

        @NotBlank(message = "password is required") @Size(min = 8, max = 50, message = "password must be between 8 and 50 characters") @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{8,50}$", message = "password must be 8-50 chars, contain letters and numbers, and include at least one uppercase letter") String password) {
}

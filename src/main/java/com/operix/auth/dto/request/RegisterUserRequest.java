package com.operix.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotEmpty(message = "Nome é obrigatório") String username,
        @NotEmpty(message = "Email é obrigatório") @Email(message = "Email inválido") String email,
        @NotEmpty(message = "Senha é obrigatória") @NotBlank @Size(min = 8) String password) {
}

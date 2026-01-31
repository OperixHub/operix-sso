package com.operix.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(
                @NotEmpty(message = "Nome é obrigatório") String name,
                @NotEmpty(message = "Email é obrigatório") @Email(message = "Email inválido") String email,
                @NotEmpty(message = "Senha é obrigatória") String password) {
}

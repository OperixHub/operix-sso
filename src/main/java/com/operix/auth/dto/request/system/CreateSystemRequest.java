package com.operix.auth.dto.request.system;

import jakarta.validation.constraints.NotEmpty;

public record CreateSystemRequest(
                @NotEmpty(message = "Nome é obrigatório") String name,
                @NotEmpty(message = "URI é obrigatória") String uri) {
}

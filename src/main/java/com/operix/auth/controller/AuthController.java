package com.operix.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.dto.request.RegisterUserRequest;
import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.service.KeycloakAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "")
public class AuthController {
    private final KeycloakAdminService keycloakAdminService;

    public AuthController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Registra um novo usuário no sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterUserRequest request) {
        try {
            boolean created = keycloakAdminService.createUser(request);
            if (created) {
                return ResponseEntity.ok(ApiResponse.success("Usuário criado com sucesso", null));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Usuário não pôde ser criado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erro ao criar usuário: " + e.getMessage()));
        }

    }

}

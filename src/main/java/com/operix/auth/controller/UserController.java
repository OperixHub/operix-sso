package com.operix.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.operix.auth.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
public class UserController {

    @GetMapping("/profile")
    @Operation(summary = "Dados do usuário logado", description = "Retorna informações do token JWT (Keycloak)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Object roles = realmAccess != null ? realmAccess.get("roles") : Collections.emptyList();
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) {
            username = jwt.getClaimAsString("username");
        }

        Map<String, Object> profile = Map.of(
                "id", jwt.getSubject(),
                "username", username,
                "email", jwt.getClaimAsString("email"),
                "roles", roles);
        return ResponseEntity.ok(ApiResponse.success("Dados do usuário", profile));
    }
}
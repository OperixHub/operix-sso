package com.operix.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.operix.auth.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
public class UserController {

    @GetMapping("/profile")
    @Operation(summary = "Dados do usuário logado", description = "Retorna informações do token JWT (Keycloak)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> profile = Map.of(
                "id", jwt.getSubject(),
                "username", jwt.getClaim("username"),
                "email", jwt.getClaim("email"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles"));
        return ResponseEntity.ok(ApiResponse.success("Dados do usuário", profile));
    }
}
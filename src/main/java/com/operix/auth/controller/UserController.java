package com.operix.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.entity.UserProfile;
import com.operix.auth.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // Endpoint que retorna dados do JWT (Keycloak) – não consulta banco local
    @GetMapping("/profile")
    @Operation(summary = "Dados do usuário logado", description = "Retorna informações do token JWT (Keycloak)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> profile = Map.of(
            "id", jwt.getSubject(),
            "username", jwt.getClaim("preferred_username"),
            "email", jwt.getClaim("email"),
            "roles", jwt.getClaimAsMap("realm_access").get("roles")
        );
        return ResponseEntity.ok(ApiResponse.success("Dados do usuário", profile));
    }

    // Endpoint que retorna dados adicionais do perfil local (ex.: preferências)
    // Necessário ter uma entidade UserProfile com campo keycloakId
    @GetMapping("/profile/details")
    @Operation(summary = "Detalhes do perfil local", description = "Retorna dados adicionais do perfil (banco local)")
    public ResponseEntity<ApiResponse<UserProfile>> getProfileDetails(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        UserProfile profile = userProfileService.findByKeycloakId(keycloakId);
        return ResponseEntity.ok(ApiResponse.success("Detalhes do perfil", profile));
    }

    // Endpoint admin – lista todos os perfis locais (não os usuários do Keycloak)
    @GetMapping
    @Operation(summary = "Lista todos os perfis", description = "Retorna todos os perfis locais (apenas administradores)")
    public ResponseEntity<ApiResponse<List<UserProfile>>> getAllProfiles() {
        List<UserProfile> profiles = userProfileService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Perfis listados com sucesso!", profiles));
    }

    // Endpoint admin – busca perfil por ID local
    @GetMapping("/{id}")
    @Operation(summary = "Busca perfil por ID", description = "Retorna um perfil local (apenas administradores)")
    public ResponseEntity<ApiResponse<UserProfile>> getProfileById(@PathVariable Long id) {
        UserProfile profile = userProfileService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Perfil encontrado!", profile));
    }
}
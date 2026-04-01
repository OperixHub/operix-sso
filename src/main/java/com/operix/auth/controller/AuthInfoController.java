package com.operix.auth.controller;

import com.operix.auth.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints utilitários do Resource Server")
public class AuthInfoController {

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário autenticado", description = "Retorna informações úteis do JWT validado pelo Resource Server")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success("Token válido", baseClaims(jwt)));
    }

    @GetMapping("/claims")
    @Operation(summary = "Claims do token", description = "Retorna um subconjunto seguro das claims do JWT (sem dados sensíveis)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> claims(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success("Claims do token", baseClaims(jwt)));
    }

    @GetMapping("/authorities")
    @Operation(summary = "Authorities/roles", description = "Retorna as authorities já mapeadas pelo Spring Security")
    public ResponseEntity<ApiResponse<Map<String, Object>>> authorities(org.springframework.security.core.Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Authorities do usuário", Map.of("authorities", authorities)));
    }

    private Map<String, Object> baseClaims(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Object roles = realmAccess != null ? realmAccess.get("roles") : List.of();

        Instant exp = jwt.getExpiresAt();
        Instant iat = jwt.getIssuedAt();

        Map<String, Object> data = new HashMap<>();
        data.put("subject", jwt.getSubject());
        data.put("issuer", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null);
        data.put("audience", jwt.getAudience());
        data.put("issuedAt", iat != null ? iat.toString() : null);
        data.put("expiresAt", exp != null ? exp.toString() : null);
        data.put("preferredUsername", jwt.getClaimAsString("preferred_username"));
        data.put("email", jwt.getClaimAsString("email"));
        data.put("roles", roles);
        return data;
    }
}


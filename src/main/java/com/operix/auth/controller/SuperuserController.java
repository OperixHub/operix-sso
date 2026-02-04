package com.operix.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.entity.Superuser;
import com.operix.auth.service.SuperuserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/superusers")
@Tag(name = "Superusers", description = "Endpoints de super usuários")
public class SuperuserController {

    private final SuperuserService superuserService;

    public SuperuserController(SuperuserService superuserService) {
        this.superuserService = superuserService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os super usuários", description = "Retorna todos os super usuários")
    public ResponseEntity<ApiResponse<List<Superuser>>> getAllSuperusers() {
        List<Superuser> superusers = superuserService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Super usuários listados com sucesso!", superusers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lista um usuário", description = "Retorna um super usuário")
    public ResponseEntity<ApiResponse<Superuser>> getUserById(@PathVariable Long id) {
        Superuser superusers = superuserService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Super usuário listado com sucesso!", superusers));
    }

}
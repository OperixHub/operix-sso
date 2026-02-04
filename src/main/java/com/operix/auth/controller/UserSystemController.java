package com.operix.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.entity.UserSystem;
import com.operix.auth.service.UserSystemService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users-systems")
@Tag(name = "Usuários Sistemas", description = "Endpoints de relacionamentos entre usuários e sistemas")
public class UserSystemController {

    private final UserSystemService userSystemService;

    public UserSystemController(UserSystemService userSystemService) 
    {
        this.userSystemService = userSystemService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os relacionamentos entre usuários e sistemas", description = "Retorna todos os relacionamentos entre usuários e sistemas")
    public ResponseEntity<ApiResponse<List<UserSystem>>> getAllUserSystems() {
        List<UserSystem> userSystems = userSystemService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Relacionamentos entre usuários e sistemas listados com sucesso!", userSystems));
    }

}

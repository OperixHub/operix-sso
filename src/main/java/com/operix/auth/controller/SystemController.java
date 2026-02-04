package com.operix.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.dto.request.system.CreateSystemRequest;
import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.entity.System;
import com.operix.auth.service.SystemService;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/systems")
@Tag(name = "Sistemas", description = "Endpoints de sistemas")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) 
    {
        this.systemService = systemService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os sistemas", description = "Retorna todos os sistemas")
    public ResponseEntity<ApiResponse<List<System>>> getAllSystems() {
        List<System> systems = systemService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Sistemas listados com sucesso!", systems));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lista um sistema", description = "Retorna um sistema")
    public ResponseEntity<ApiResponse<System>> getSystemById(Long id) {
        System system = systemService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Sistema listado com sucesso!", system));
    }

    @PostMapping
    @Operation(summary = "Cria um sistema", description = "Cria um sistema")
    public ResponseEntity<ApiResponse<System>> createSystem(@Valid @RequestBody CreateSystemRequest request) {
        System system = new System();
        system.setName(request.name());
        system.setUri(request.uri());
        systemService.save(system);
        return ResponseEntity.ok(ApiResponse.success("Sistema criado com sucesso!", system));
    }

}

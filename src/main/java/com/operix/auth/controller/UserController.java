package com.operix.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.entity.User;
import com.operix.auth.service.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Endpoints de usuários")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) 
    {
        this.userService = userService;
    }

    @GetMapping("/")
    @Operation(summary = "Lista todos os usuários", description = "Retorna todos os usuários")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Usuários listados com sucesso!", users));
    }

}
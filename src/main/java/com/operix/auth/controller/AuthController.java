package com.operix.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.config.TokenConfig;
import com.operix.auth.dto.request.LoginRequest;
import com.operix.auth.dto.request.RegisterUserRequest;
import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.dto.response.LoginResponse;
import com.operix.auth.dto.response.RegisterUserResponse;
import com.operix.auth.entity.User;
import com.operix.auth.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de login e registro de usuários")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, TokenConfig tokenConfig) 
    {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica o usuário", description = "Recebe credenciais e retorna token JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(),
                request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso!", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Registra o usuário", description = "Cria um novo usuário no sistema")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());

        userService.save(user);

        RegisterUserResponse response = new RegisterUserResponse(
                user.getName(),
                user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuário registrado com sucesso!", response));
    }
    

}
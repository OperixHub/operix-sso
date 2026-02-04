package com.operix.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operix.auth.config.security.TokenConfig;
import com.operix.auth.dto.request.LoginRequest;
import com.operix.auth.dto.request.RegisterUserRequest;
import com.operix.auth.dto.response.ApiResponse;
import com.operix.auth.dto.response.LoginResponse;
import com.operix.auth.dto.response.RegisterUserResponse;
import com.operix.auth.entity.User;
import com.operix.auth.entity.UserSystem;
import com.operix.auth.service.UserService;
import com.operix.auth.service.UserSystemService;
import com.operix.auth.entity.Superuser;
import com.operix.auth.service.SuperuserService;
import com.operix.auth.entity.System;
import com.operix.auth.service.SystemService;

import jakarta.servlet.http.HttpServletRequest;
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
    private final SuperuserService superuserService;
    private final SystemService systemService;
    private final UserSystemService userSystemService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, TokenConfig tokenConfig,
            SuperuserService superuserService, SystemService systemService, UserSystemService userSystemService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.superuserService = superuserService;
        this.systemService = systemService;
        this.userSystemService = userSystemService;
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
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(HttpServletRequest clientRequest, @Valid @RequestBody RegisterUserRequest request) {
        
        String origin = clientRequest.getHeader("Origin");
        System system = systemService.findByUri(origin);

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());

        User userSaved = userService.save(user);

        UserSystem userSystem = new UserSystem();
        userSystem.setUserId(userSaved.getId());
        userSystem.setSystemId(system.getId());

        userSystemService.save(userSystem);

        RegisterUserResponse response = new RegisterUserResponse(
                userSaved.getName(),
                userSaved.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuário registrado com sucesso!", response));
    }

    @PostMapping("/su/login")
    @Operation(summary = "Autentica o superuser", description = "Recebe credenciais e retorna token JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> suLogin(@Valid @RequestBody LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(),
                request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        Superuser superuser = (Superuser) authentication.getPrincipal();
        String token = tokenConfig.generateToken(superuser);

        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso!", response));
    }

    @PostMapping("/su/register")
    @Operation(summary = "Registra o superuser", description = "Cria um novo superuser no sistema")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> suRegister(@Valid @RequestBody RegisterUserRequest request) {

        Superuser superuser = new Superuser();
        superuser.setName(request.name());
        superuser.setEmail(request.email());
        superuser.setPassword(request.password());

        superuserService.save(superuser);

        RegisterUserResponse response = new RegisterUserResponse(
                superuser.getName(),
                superuser.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Superuser registrado com sucesso!", response));
    }

}
package com.operix.auth.security;

import com.operix.auth.service.KeycloakAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityIntegrationTest.TestBeans.class)
class SecurityIntegrationTest {

    MockMvc mockMvc;

    @Autowired
    KeycloakAdminService keycloakAdminService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void profile_sem_token_deve_retornar_401() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profile_com_jwt_deve_retornar_dados_basicos_e_roles() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("user-123")
                                .claim("email", "user@example.com")
                                .claim("preferred_username", "jp")
                                .claim("realm_access", Map.of("roles", List.of("admin", "user"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value("user-123"))
                .andExpect(jsonPath("$.data.username").value("jp"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.roles[0]").value("admin"));
    }

    @Test
    void me_deve_retornar_subject_e_roles_quando_autenticado() throws Exception {
        // sanity: garantir que o endpoint está mapeado
        boolean hasMeMapping = requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
                .flatMap(info -> info.getDirectPaths().stream())
                .anyMatch("/api/v1/auth/me"::equals);
        org.junit.jupiter.api.Assertions.assertTrue(hasMeMapping, "Mapping /api/v1/auth/me não registrado");

        mockMvc.perform(get("/api/v1/auth/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("user-123")
                                .claim("preferred_username", "jp")
                                .claim("email", "user@example.com")
                                .claim("realm_access", Map.of("roles", List.of("user"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.subject").value("user-123"))
                .andExpect(jsonPath("$.data.roles[0]").value("user"));
    }

    @Test
    void register_deve_ser_publico_e_retornar_200_quando_criar_usuario() throws Exception {
        when(keycloakAdminService.createUser(any())).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "user1",
                                  "email": "user1@example.com",
                                  "password": "Senha@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    static class TestBeans {
        @Bean
        @Primary
        KeycloakAdminService keycloakAdminService() {
            return mock(KeycloakAdminService.class);
        }
    }
}


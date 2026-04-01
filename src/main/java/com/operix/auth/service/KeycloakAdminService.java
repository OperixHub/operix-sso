package com.operix.auth.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.operix.auth.dto.request.RegisterUserRequest;

import javax.ws.rs.core.Response;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminService(Keycloak keycloak,
            @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public boolean createUser(RegisterUserRequest request) throws Exception {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEnabled(true);

        Response response = keycloak.realm(realm).users().create(user);
        try {
            int status = response.getStatus();

            if (status == 201) {
                String location = response.getHeaderString("Location");
                String userId = location.substring(location.lastIndexOf('/') + 1);

                CredentialRepresentation cred = new CredentialRepresentation();
                cred.setTemporary(false);
                cred.setType(CredentialRepresentation.PASSWORD);
                cred.setValue(request.password());

                keycloak.realm(realm).users().get(userId).resetPassword(cred);
                return true;
            } else if (status == 409) {
                throw new Exception("Usuário já existe");
            }

            throw new IllegalStateException("Erro ao criar usuário no Keycloak: HTTP " + status);

        } finally {
            response.close();
        }
    }
}
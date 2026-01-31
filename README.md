

## ⚙️ Configuração

### Banco de Dados

Certifique-se de ter um banco de dados MySQL rodando e configure as credenciais em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/operix_auth
spring.datasource.username=root
spring.datasource.password=
```

### Documentação

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🚀 Como Rodar

### Pré-requisitos

- Java 21
- Postgres

### Execução

```bash
./mvnw spring-boot:run
```

### Testes

```bash
./mvnw test
```

## 🔐 Segurança

### Validação de Token

Todos os endpoints, exceto os de autenticação, requerem um token JWT válido no header `Authorization`:

```
Authorization: Bearer <token>

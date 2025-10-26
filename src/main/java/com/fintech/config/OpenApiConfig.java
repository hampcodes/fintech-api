package com.fintech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API.
 *
 * La documentación estará disponible en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fintechOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080/api/v1");
        localServer.setDescription("Servidor de desarrollo local");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.fintech.com/api/v1");
        prodServer.setDescription("Servidor de producción");

        Contact contact = new Contact();
        contact.setName("Equipo Fintech API");
        contact.setEmail("support@fintech.com");
        contact.setUrl("https://www.fintech.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Fintech API - Sistema Bancario")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para operaciones bancarias básicas desarrollada con Spring Boot 3.5.6 y Java 21.\n\n" +
                        "## Características principales:\n" +
                        "- Gestión completa de cuentas bancarias\n" +
                        "- Procesamiento de transacciones (depósitos y retiros)\n" +
                        "- Validaciones de negocio robustas\n" +
                        "- Manejo global de excepciones\n" +
                        "- API REST versionada (/api/v1)\n\n" +
                        "## Tecnologías:\n" +
                        "- **Java 21**\n" +
                        "- **Spring Boot 3.5.6**\n" +
                        "- **PostgreSQL** (Producción)\n" +
                        "- **H2 Database** (Tests)\n" +
                        "- **JUnit 5 & Mockito** (Testing)")
                .termsOfService("https://www.fintech.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, prodServer));
    }
}

package com.itb.inf2fm.projetoback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                    new Server().url("http://localhost:" + serverPort).description("Servidor Local"),
                    new Server().url("http://127.0.0.1:" + serverPort).description("Servidor Local (IP)")
                ))
                .info(new Info()
                        .title("Projeto Backend API")
                        .description("API REST para gerenciamento de usuários, técnicos, clientes, especialidades e regiões. " +
                                    "Desenvolvida com Spring Boot 3.4.0 e Java 21. " +
                                    "Integração disponível para Flutter e ReactJS.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@itb.inf2fm.com")
                                .url("https://github.com/RenanSilvaMartins/projetoback"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}

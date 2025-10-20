package br.com.fiap.challenge.gamblers.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Gamblers API", version = "v1", description = "API para gerenciar usuários e transações - documentação automática gerada pelo SpringDoc"),
        servers = {@Server(url = "/", description = "Local server")}
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Gamblers API")
                        .version("v1")
                        .description("API para gerenciar usuários e transações - documentação automática gerada pelo SpringDoc")
                        .contact(new io.swagger.v3.oas.models.info.Contact().name("FIAP Challenge").email("noreply@example.com"))
                        .license(new io.swagger.v3.oas.models.info.License().name("MIT"))
                );
    }
}

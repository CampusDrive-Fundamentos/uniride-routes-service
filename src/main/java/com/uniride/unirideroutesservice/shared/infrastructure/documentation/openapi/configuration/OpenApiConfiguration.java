package com.uniride.unirideroutesservice.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI routingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("UniRide Routes API")
                        .description("API de enrutamiento para la plataforma CampusDrive.")
                        .version("v1.0.0"));
    }
}
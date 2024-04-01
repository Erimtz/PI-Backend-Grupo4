package com.gym.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lightweight Gym App API")
                        .version("1.0-SNAPSHOT")
                        .description("API del Proyecto Integrador 2 del Grupo 4 de un E-Commerce para el gimnasio Lightweight desarrollada con Spring")
                        .contact(new io.swagger.v3.oas.models.info.Contact().url("https://twitter.com/lightweightgym").email("lightweightgym@gmail.com"))
                        .license(new io.swagger.v3.oas.models.info.License().name("All rights reserved").url("URL de license"))
                        .termsOfService("terms Of Service"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }

}
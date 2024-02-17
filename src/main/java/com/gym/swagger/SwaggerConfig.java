package com.gym.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI api(){
        return new OpenAPI().info(new Info().title("Lightweight Gym App API")
                .version("1.0-SNAPSHOT").description("API del Proyecto Integrador 2 del Grupo 4 de un E-Commerce para el gimnasio Lightweight desarrollada con Spring")
                .contact(new Contact().url("https://twitter.com/lightweightgym").email("lightweightgym@gmail.com").name("Grupo 4"))
                .license(new License().name("Todos los derechos reservados").url("URL de license")).termsOfService("terms Of Service"));
    }

}
package com.tiketeer.Tiketeer.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Tiketeer API Swagger", description = "API 명세를 위한 Swagger 페이지")
)
@Configuration
public class SwaggerConfig {
}

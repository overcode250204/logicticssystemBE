package com.overcode250204.smartlogicticssystem.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Logistic System API") // Tên dự án của bạn
                        .version("1.0.0")
                        .description("Smart Logistic System API Documentation")
                        .contact(new Contact()
                                .name("Overcode250204")
                                .email("nguyenlpkse182643@fpt.edu.vn"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}

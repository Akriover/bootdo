package com.example.usercrud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置 — Swagger UI 元数据
 *
 * 访问:
 *   - Swagger UI: http://localhost:8080/swagger-ui.html
 *   - OpenAPI JSON: http://localhost:8080/v3/api-docs
 *   - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userCrudOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User CRUD API")
                        .description("简易用户信息录入系统 — Vibe coding 实战 #1 (Spring Boot 3.5 + JPA)")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Klukay")
                                .url("https://github.com/Akriover/bootdo"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

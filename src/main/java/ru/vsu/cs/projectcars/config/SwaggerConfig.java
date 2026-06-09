package ru.vsu.cs.projectcars.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI projectCarsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ProjectCars Marketplace API")
                        .description("Платформа агрегации и поиска проектных автомобилей")
                        .version("2.0.0")
                        .contact(new Contact().name("Разработчик"))
                        .license(new License().name("Apache 2.0")));
    }
}

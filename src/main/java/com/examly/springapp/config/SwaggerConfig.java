package com.examly.springapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Delivery Application API")
                        .description("API documentation for Food Delivery Application")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Murugaprakash S")
                                .url("https://yourwebsite.com")
                                .email("murugaprakashs104@gmail.com")));
    }
}
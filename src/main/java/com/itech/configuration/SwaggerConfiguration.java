package com.itech.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itech"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfoBuilder()
                                .title("Bank web application")
                                .description("Free to use")
                                .contact( new springfox.documentation.service.Contact("Krainiy Edvard", "https://www.linkedin.com/in/edvard-krainiy-5b2b13221/", "a@b.com"))
                                .version("1.0")
                                .license("API License")
                                .build());
    }
}
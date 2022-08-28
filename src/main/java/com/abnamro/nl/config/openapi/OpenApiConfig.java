package com.abnamro.nl.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final OpenApiConstants openApiConstants;

    @Bean
    public OpenAPI recipeManagementOpenApi() {
        return new OpenAPI()
                .info(new Info().title(openApiConstants.getTitle())
                        .description(openApiConstants.getDescription())
                        .version(openApiConstants.getVersion())
                        .contact(getContact()));
    }

    private Contact getContact() {
        Contact contact = new Contact();
        contact.setEmail(openApiConstants.getContact().getEmail());
        contact.setName(openApiConstants.getContact().getTeamName());
        return contact;
    }
}

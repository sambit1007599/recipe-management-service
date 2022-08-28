package com.abnamro.nl.config.openapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The type Swagger constants.
 */
@Component
@ConfigurationProperties("open.api")
@Getter
@Setter
public class OpenApiConstants {

    private String title;
    private String description;
    private String version;
    private Contact contact;
}

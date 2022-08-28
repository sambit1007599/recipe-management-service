package com.abnamro.nl.config;

import com.abnamro.nl.models.ErrorAttributes;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties("constants.error")
@Getter
@Setter
public class ErrorConstants {

    private ErrorAttributes noRecipeFoundException;

}

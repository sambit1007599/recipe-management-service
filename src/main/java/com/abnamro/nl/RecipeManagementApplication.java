package com.abnamro.nl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class RecipeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeManagementApplication.class, args);
    }
}

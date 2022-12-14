package com.abnamro.nl.entity;

import com.abnamro.nl.models.RecipeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    private String id;

    @NotBlank(message = "name is mandatory")
    private String name;

    @NotNull(message = "servings is mandatory")
    private int servings;

    @NotNull(message = "recipe type is mandatory")
    private RecipeType type;

    private List<String> ingredients;

    @TextIndexed(weight = 2)
    private String instructions;

}

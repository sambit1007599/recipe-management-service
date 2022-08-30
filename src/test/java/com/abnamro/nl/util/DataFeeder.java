package com.abnamro.nl.util;

import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.repository.RecipeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@UtilityClass
public class DataFeeder {

    @Autowired
    RecipeRepository recipeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Recipe> getRecipeData() {
        try {
            return objectMapper
                    .readValue(new File(DataFeeder.class.getClassLoader().getResource("data/recipe_data_list.json")
                            .getFile()), new TypeReference<>() {
                    });

        } catch (IOException e) {
            log.error("IO exception occurred while retrieving recipe Data {} ", e);
            throw new RuntimeException(e);
        }
    }

    public Recipe convertJsonStringToObject(String str) {
        try {
            return objectMapper.readValue(str, Recipe.class);
        } catch (IOException e) {
            log.error("IO exception occurred while converting json string Data {} ", e);
            throw new RuntimeException(e);
        }
    }

    public List<Recipe> convertJsonStringToListOfObject(String str) {
        try {
            return objectMapper.readValue(str, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("IO exception occurred while converting json string Data {} ", e);
            throw new RuntimeException(e);
        }
    }

    public String getRecipeDataAsString(Recipe recipe) {
        try {
            return objectMapper.writeValueAsString(recipe);
        } catch (IOException e) {
            log.error("IO exception occurred while converting Object to json string Data {} ", e);
            throw new RuntimeException(e);
        }
    }


}

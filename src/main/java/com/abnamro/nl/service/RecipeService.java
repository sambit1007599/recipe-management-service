package com.abnamro.nl.service;

import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.exception.NoRecipeFoundException;
import com.abnamro.nl.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public Recipe getRecipe(String id) {
        return recipeRepository.findById(id).orElseThrow(() -> new NoRecipeFoundException(id));
    }

    public List<Recipe> addRecipes(List<Recipe> recipeList) {
        return recipeRepository.saveAll(recipeList);
    }

    public void updateRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public void deleteRecipe(String id) {
        recipeRepository.deleteById(id);
    }


}

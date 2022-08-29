package com.abnamro.nl.service;

import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.exception.NoRecipeFoundException;
import com.abnamro.nl.models.RecipeType;
import com.abnamro.nl.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MongoTemplate mongoTemplate;

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

    public List<Recipe> searchRecipes(RecipeType type, Integer servings, String includedIngredients,
                                      String excludeIngredients, String instructions) {
        Query query = new Query();
        Optional.ofNullable(type).ifPresent(t -> query.addCriteria(Criteria.where("type").is(t.name())));
        Optional.ofNullable(servings).ifPresent(s -> query.addCriteria(Criteria.where("servings").is(s)));

        Criteria ingredientCriteria = Criteria.where("ingredients");
        Optional.ofNullable(includedIngredients).ifPresent(ingredientCriteria::in);
        Optional.ofNullable(excludeIngredients).ifPresent(ingredientCriteria::nin);
        if (includedIngredients != null || excludeIngredients != null) {
            query.addCriteria(ingredientCriteria);
        }

        Optional.ofNullable(instructions).ifPresent(inst -> query
                .addCriteria(TextCriteria.forDefaultLanguage().caseSensitive(false).matchingAny(inst)));

        return mongoTemplate.find(query, Recipe.class);
    }

}

package com.abnamro.nl.controller;

import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.models.RecipeType;
import com.abnamro.nl.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping(value = "/recipe/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> getRecipeById(@PathVariable("id") String id) {
        return new ResponseEntity<>(recipeService.getRecipe(id), HttpStatus.OK);
    }

    @GetMapping(value = "/recipe/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(value = "type", required = false) RecipeType type,
            @RequestParam(value = "servings", required = false) Integer servings,
            @RequestParam(value = "includedIngredients", required = false) String includedIngredients,
            @RequestParam(value = "excludedIngredients", required = false) String excludedIngredients,
            @RequestParam(value = "instructions", required = false) String instructions
    ) {
        return new ResponseEntity<>(
                recipeService.searchRecipes(type, servings, includedIngredients, excludedIngredients, instructions),
                HttpStatus.OK);
    }

    @PostMapping(value = "/recipes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Recipe>> createRecipe(@RequestBody @Valid List<Recipe> recipeList) {
        return new ResponseEntity<>(recipeService.addRecipes(recipeList), HttpStatus.CREATED);
    }

    @PutMapping(value = "/recipe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRecipe(@RequestBody @Valid Recipe recipe) {
        recipeService.updateRecipe(recipe);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity deleteRecipe(@PathVariable("id") String id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.abnamro.nl.controller;

import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping(value = "/recipe/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> getRecipe(@PathVariable("id") String id) {
        return new ResponseEntity<>(recipeService.getRecipe(id), HttpStatus.OK);
    }

    @PostMapping(value = "/recipes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Recipe>> createRecipe(@RequestBody List<Recipe> recipeList) {
        return new ResponseEntity<>(recipeService.addRecipes(recipeList), HttpStatus.CREATED);
    }

    @PutMapping(value = "/recipe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRecipe(@RequestBody Recipe recipe) {
        recipeService.updateRecipe(recipe);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity deleteRecipe(@PathVariable("id") String id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

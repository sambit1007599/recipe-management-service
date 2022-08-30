package com.abnamro.nl.service;

import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.exception.NoRecipeFoundException;
import com.abnamro.nl.models.RecipeType;
import com.abnamro.nl.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.abnamro.nl.models.RecipeType.NON_VEGETARIAN;
import static com.abnamro.nl.models.RecipeType.VEGETARIAN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private Recipe recipe;

    private RecipeService recipeService;

    private final String id = "id1";

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(recipeRepository, mongoTemplate);
    }

    @Test
    @DisplayName("Test Retrieve recipe by Id : success")
    void testGetRecipeSuccessfully() {
        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
        Recipe recipeResponse = recipeService.getRecipe(id);
        assertThat(recipeResponse).isEqualTo(recipe);
        verify(recipeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test Retrieve recipe by Id : NoRecipeFoundException")
    void testThrowExceptionWhileRetrieveRecipe() {
        String message = "No Recipe Found";
        when(recipeRepository.findById(id)).thenThrow(new NoRecipeFoundException(message));
        Exception exception = assertThrows(NoRecipeFoundException.class, () -> recipeService.getRecipe(id));
        assertThat(exception.getMessage()).isEqualTo(message);
        verify(recipeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test Adding Recipes")
    void testAddRecipes() {
        when(recipeRepository.saveAll(List.of(recipe))).thenReturn(List.of(recipe));
        List<Recipe> recipeResponse = recipeService.addRecipes(List.of(recipe));
        assertThat(recipeResponse.size()).isEqualTo(1);
        assertThat(recipeResponse).isEqualTo(List.of(recipe));
        verify(recipeRepository, times(1)).saveAll(List.of(recipe));
    }

    @Test
    @DisplayName("Test updating Recipe")
    void testUpdateRecipe() {
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        recipeService.updateRecipe(recipe);
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    @DisplayName("Test delete Recipe by Id")
    void testDeleteRecipe() {
        doNothing().when(recipeRepository).deleteById(id);
        recipeService.deleteRecipe(id);
        verify(recipeRepository, times(1)).deleteById(id);
    }

    @ParameterizedTest(name = "#{index} - Test search recipe with: {0},{1},{2},{3},{4}")
    @MethodSource("userDataProvider")
    void testSearchRecipes(RecipeType type, Integer servings, String includedIngredients,
                           String excludeIngredients, String instructions) {
        Query query = createQuery(type, servings, includedIngredients, excludeIngredients, instructions);
        when(mongoTemplate.find(query, Recipe.class)).thenReturn(List.of(recipe));
        recipeService.searchRecipes(type, servings, includedIngredients, excludeIngredients, instructions);
        verify(mongoTemplate, times(1)).find(query, Recipe.class);
    }

    private Query createQuery(RecipeType type, Integer servings, String includedIngredients,
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

        return query;
    }

    private static Stream<Arguments> userDataProvider() {
        return Stream.of(
                Arguments.of(VEGETARIAN, 4, "chicken", "peas", "oven"),
                Arguments.of(NON_VEGETARIAN, null, null, null, null),
                Arguments.of(VEGETARIAN, 2, "chicken", null, null),
                Arguments.of(null, null, "chicken", "peas", "oven"),
                Arguments.of(null, 4, null, "peas", null)
        );
    }
}
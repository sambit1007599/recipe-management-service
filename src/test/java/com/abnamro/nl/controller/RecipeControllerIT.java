package com.abnamro.nl.controller;

import com.abnamro.nl.RecipeManagementApplication;
import com.abnamro.nl.entity.Recipe;
import com.abnamro.nl.exception.NoRecipeFoundException;
import com.abnamro.nl.repository.RecipeRepository;
import com.abnamro.nl.util.DataFeeder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.entity.mime.MIME;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static com.abnamro.nl.models.RecipeType.VEGETARIAN;
import static com.mongodb.assertions.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = RecipeManagementApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class RecipeControllerIT {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port = 0;

    private static final String GET_URI = "/v1/recipe/{id}";
    private static final String POST_URI = "/v1/recipes";
    private static final String PUT_URI = "/v1/recipe";
    private static final String DELETE_URI = "/v1/recipe/{id}";
    private static final String SEARCH_URI = "/v1/search/recipe";


    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void initData() {
        List<Recipe> recipeList = DataFeeder.getRecipeData();
        recipeRepository.saveAll(recipeList);
    }

    @AfterEach
    void cleanUp() {
        recipeRepository.deleteAll();
    }

    @Test
    @DisplayName("Test getRecipe by id flow")
    void testGetRecipeById() throws Exception {
        String result = mockMvc
                .perform(MockMvcRequestBuilders.get(GET_URI, 1).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Recipe recipe = DataFeeder.convertJsonStringToObject(result);
        assertThat(recipe.getId()).isEqualTo("1");
        assertThat(recipe).isEqualTo(DataFeeder.getRecipeData().get(0));
    }

    @Test
    @DisplayName("Test getRecipe by id flow : NoRecipeFound Exception")
    void testGetRecipeByIdFailure() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(GET_URI, 5).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(r -> assertTrue(r.getResolvedException() instanceof NoRecipeFoundException))
                .andExpect(r -> assertThat(r.getResolvedException().getMessage()).isEqualTo("5"));
    }

    @Test
    @DisplayName("Test create recipe flow")
    void testCreateRecipe() {
        Recipe recipe = DataFeeder.getRecipeData().get(0);
        recipe.setId("5");

        int statusCode = given()
                .header(MIME.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .and()
                .accept(ContentType.JSON)
                .body(List.of(recipe))
                .when()
                .post(POST_URI)
                .then().extract().response().statusCode();
        assertThat(statusCode).isEqualTo(201);
        assertThat(recipeRepository.findById("5").get()).isNotNull();
    }

    @Test
    @DisplayName("Test update recipe flow")
    void testUpdateRecipe() throws Exception {
        Recipe recipe = recipeRepository.findById("4").get();
        recipe.setName("new-recipe");
        mockMvc.perform(MockMvcRequestBuilders
                        .put(PUT_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DataFeeder.getRecipeDataAsString(recipe)))
                .andExpect(status().isOk());
        assertThat(recipeRepository.findById("4").get().getName()).isEqualTo("new-recipe");

    }

    @Test
    @DisplayName("Test delete recipe flow")
    void testDeleteRecipe() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(DELETE_URI, 3)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
        Optional<Recipe> recipe = recipeRepository.findById("3");
        assertThat(recipe.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Test search recipe with all vegetarian dishes")
    void searchRecipesCriteria1() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders
                        .get(SEARCH_URI)
                        .queryParam("type", VEGETARIAN.name())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Recipe> recipeList = DataFeeder.convertJsonStringToListOfObject(result);
        assertThat(recipeList.size()).isEqualTo(2);
        recipeList.forEach(r -> assertThat(r.getType()).isEqualTo(VEGETARIAN));
    }

    @Test
    @DisplayName("Test search recipe with number of servings 4 and with “potato” as ingredient")
    void searchRecipesCriteria2() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders
                        .get(SEARCH_URI)
                        .queryParam("servings", "4")
                        .queryParam("includedIngredients", "potato")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Recipe> recipeList = DataFeeder.convertJsonStringToListOfObject(result);
        assertThat(recipeList.size()).isEqualTo(1);
        recipeList.forEach(r -> {
            assertThat(r.getServings()).isEqualTo(4);
            assertThat(r.getIngredients().contains("potato")).isTrue();
        });
    }

    @Test
    @DisplayName("Test search recipe without “salmon” as an ingredient that has “oven” in the instructions")
    void searchRecipesCriteria3() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders
                        .get(SEARCH_URI)
                        .queryParam("excludedIngredients", "salmon")
                        .queryParam("instructions", "oven")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Recipe> recipeList = DataFeeder.convertJsonStringToListOfObject(result);
        assertThat(recipeList.size()).isEqualTo(1);
        recipeList.forEach(r -> {
            assertThat(r.getIngredients().contains("salmon")).isFalse();
            assertThat(r.getInstructions().contains("oven")).isTrue();
        });
    }
}
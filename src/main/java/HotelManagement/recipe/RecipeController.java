package HotelManagement.recipe;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private MakingRecipeService makingRecipeService;

    // Create a new recipe
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Recipe>> addRecipe(@RequestBody RecipeDto recipeDto) {
        ApiResponse<Recipe> response = new ApiResponse<>();
        try {
            Recipe recipe = makingRecipeService.addRecipe(recipeDto);
            response.setMessage("Recipe created successfully");
            response.setEntity(recipe);
            response.setStatusCode(HttpStatus.CREATED.value());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.setMessage("Error: " + e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/details")  // Endpoint to retrieve recipes with ingredients and spices
//    public ResponseEntity<ApiResponse<List<RecipeSpiceIngredientCostDTO>>> getAllRecipesWithIngredientsAndSpices() {
//        ApiResponse response = new ApiResponse<>();
//        List<RecipeSpiceIngredientCostDTO> recipesWithDetails = makingRecipeService.getAllRecipesWithIngredientsAndSpices();
//        response.setMessage("recipes retrived successfully");
//        response.setStatusCode(HttpStatus.OK.value());
//        response.setEntity(recipesWithDetails);
//        return new ResponseEntity<>(response,HttpStatus.FOUND) ; // Return the list with HTTP 200 OK
//    }


    // Get a recipe by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Recipe>> getRecipeById(@PathVariable Long id) {
        ApiResponse<Recipe> response = new ApiResponse<>();
        Optional<Recipe> recipe = makingRecipeService.getRecipeById(id);

        if (recipe.isPresent()) {
            response.setMessage("Recipe found");
            response.setEntity(recipe.get());
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("Recipe with ID " + id + " not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Get all recipes
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Iterable<Recipe>>> getAllRecipes() {
        ApiResponse<Iterable<Recipe>> response = new ApiResponse<>();
        response.setMessage("Recipes retrieved successfully");
        response.setEntity(makingRecipeService.getAllRecipes());
        response.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Update a recipe by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Recipe>> updateRecipe(
            @PathVariable Long id, @RequestBody RecipeDto recipeDto) {
        ApiResponse<Recipe> response = new ApiResponse<>();
        try {
            Recipe updatedRecipe = makingRecipeService.updateRecipe(id, recipeDto);
            response.setMessage("Recipe updated successfully");
            response.setEntity(updatedRecipe);
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setMessage("Error: " + e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a recipe by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@PathVariable Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        try {
            makingRecipeService.deleteRecipe(id);
            response.setMessage("Recipe deleted successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setMessage("Error: " + e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

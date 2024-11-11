package HotelManagement.foodStock;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredients")
public class FoodStockController {

    @Autowired
    private FoodStockService foodStockService;

    // Create a new ingredient
    @PostMapping("createIngredient")
    public ResponseEntity<ApiResponse<FoodStock>> createIngredient(@RequestBody FoodStockDto ingredient) {
        ApiResponse response = new ApiResponse<>();
        FoodStock savedIngredient = foodStockService.createIngredient(ingredient);
        response.setMessage("Ingredient add successfully");
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(savedIngredient);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get an ingredient by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodStock>> getIngredientById(@PathVariable Long id) {
        ApiResponse<FoodStock> response = new ApiResponse<>();
        Optional<FoodStock> ingredient = foodStockService.getIngredientById(id);

        if (ingredient.isPresent()) {
            response.setMessage("Retrieved Ingredient Successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(ingredient.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("Ingredient with ID " + id + " not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    // Get all ingredients
    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodStock>>> getAllIngredients() {
        ApiResponse<List<FoodStock>> response = new ApiResponse<>();
        List<FoodStock> ingredients = foodStockService.getAllIngredients();

        if (ingredients.isEmpty()) {
            response.setMessage("No ingredients found.");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        response.setMessage("Ingredients retrieved successfully.");
        response.setEntity(ingredients);
        response.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    // Update an ingredient by ID
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodStock>> updateIngredient(
            @PathVariable Long id, @RequestBody FoodStock updatedIngredient) {
        ApiResponse<FoodStock> response = new ApiResponse<>();
        try {
            FoodStock updated = foodStockService.updateIngredient(id, updatedIngredient);
            response.setMessage("Ingredient updated successfully");
            response.setEntity(updated);
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setMessage("Ingredient with ID " + id + " not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setMessage("An error occurred while updating the ingredient: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Delete an ingredient by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIngredient(@PathVariable Long id) {
        ApiResponse<Void> response = new ApiResponse<>();

        try {
            foodStockService.deleteIngredient(id);
            response.setMessage("Ingredient deleted successfully.");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            response.setMessage("Ingredient with ID " + id + " not found.");
            response.setEntity(null);
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}

package HotelManagement.stock.foodStock;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        try {
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
        } catch (RuntimeException e) {
            response.setMessage(e.getMessage()); // Item has expired message
            response.setStatusCode(HttpStatus.GONE.value()); // 410 Gone status
            return new ResponseEntity<>(response, HttpStatus.GONE);
        }
    }
    @GetMapping("/allFoOdStockWithoutCost")
    public ResponseEntity<ApiResponse<List<FoodStock>>> allFoOdStockWithoutCost(){
        ApiResponse<List<FoodStock>> response = new ApiResponse<>();
        try{
            List<FoodStock> foodStocks = foodStockService.getAllFoodStockWithoutCost();
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Food stock without costs list ");
            response.setEntity(foodStocks);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch (Exception e){
           response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
           response.setMessage("server error please try again later");
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

        @GetMapping("/expiring-soon")
        public ResponseEntity<ApiResponse<List<FoodStock>>> foodStockExpiringSoon(
                @RequestParam(value = "date") String date) {

            ApiResponse<List<FoodStock>> response = new ApiResponse<>();

            // Parse the provided date
            LocalDateTime now;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                now = LocalDateTime.parse(date, formatter);  // Convert the date string to LocalDateTime
            } catch (Exception e) {
                // Return a bad request if the date format is invalid
                response.setStatusCode(HttpStatus.FORBIDDEN.value());
                response.setMessage("Invalid date format. Use 'yyyy-MM-dd HH:mm:ss'.");
                return ResponseEntity.badRequest().body(response);
            }

            // Set the date range to check for items expiring in the next 3 days
            LocalDateTime threeDaysLater = now.plusDays(3);

            // Fetch items expiring soon from the given date range
            List<FoodStock> expiringItems = foodStockService.getItemsExpiringInRange(now, threeDaysLater);

            if (expiringItems.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("No items expiring soon from the given date.");
                response.setEntity(expiringItems);
                return ResponseEntity.ok(response);
            } else {
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setMessage("Items expiring soon retrieved successfully.");
                response.setEntity(expiringItems);
                return ResponseEntity.ok(response);
            }
        }


    // Get all ingredients
    @GetMapping ("/allStock")
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
    @GetMapping("/getUniqueStockNames")
public  ResponseEntity<ApiResponse<List<UniqueStockNameDto>>> getAllUniqueStockNames(){
    ApiResponse<List<UniqueStockNameDto>> response = new ApiResponse<>();
    List<UniqueStockNameDto> stockNames = foodStockService.getUniqueStockNamesWithIds();
    if (stockNames.isEmpty()) {
        response.setMessage("No ingredients found.");
        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    response.setMessage("Ingredients retrieved successfully.");
    response.setEntity(stockNames);
    response.setStatusCode(HttpStatus.OK.value());
    return new ResponseEntity<>(response, HttpStatus.OK);
}

    @GetMapping("/getFoodStockDetails")
    public  ResponseEntity<ApiResponse<List<FoodStockDetailsDTO>>> getFoodStockDetails(){
        ApiResponse<List<FoodStockDetailsDTO>> response = new ApiResponse<>();
        List<FoodStockDetailsDTO> stockDetails = foodStockService.getFoodStockDetails();
        if (stockDetails.isEmpty()) {
            response.setMessage("No food in stock found found.");
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.setMessage("food in stock retrieved successfully.");
        response.setEntity(stockDetails);
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

package HotelManagement.meals;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<MealEntity>>> getAllMeals() {
        try {
            List<MealEntity> meals = mealService.getAllMeals();
            return ResponseEntity.ok(new ApiResponse<>("Meals fetched successfully", HttpStatus.OK.value(), meals));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to fetch meals", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponse<MealEntity>> getMealById(@PathVariable Long id) {
        try {
            MealEntity meal = mealService.getMealById(id);
            return ResponseEntity.ok(new ApiResponse<>("Meal fetched successfully", HttpStatus.OK.value(), meal));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Meal not found", HttpStatus.NOT_FOUND.value(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MealEntity>> createMeal(@RequestBody MealDto mealDto) {
        try {
            MealEntity meal = mealService.createMeal(mealDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Meal created successfully", HttpStatus.CREATED.value(), meal));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Invalid request", HttpStatus.BAD_REQUEST.value(), null));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MealEntity>> updateMeal(@PathVariable Long id, @RequestBody MealDto mealDto) {
        try {
            MealEntity meal = mealService.updateMeal(id, mealDto);
            return ResponseEntity.ok(new ApiResponse<>("Meal updated successfully", HttpStatus.OK.value(), meal));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Meal not found", HttpStatus.NOT_FOUND.value(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMeal(@PathVariable Long id) {
        try {
            mealService.deleteMeal(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>("Meal deleted successfully", HttpStatus.NO_CONTENT.value(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Meal not found", HttpStatus.NOT_FOUND.value(), null));
        }
    }
}

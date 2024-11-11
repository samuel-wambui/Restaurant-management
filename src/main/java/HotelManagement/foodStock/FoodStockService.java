package HotelManagement.foodStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodStockService {

    @Autowired
    private FoodStockRepo foodStockRepo;

    // Create a new ingredient
    public FoodStock createIngredient(FoodStockDto foodStockDto) {
        FoodStock ingredient = new FoodStock();
        ingredient.setName(foodStockDto.getName());
        return foodStockRepo.save(ingredient);
    }

    // Get a specific ingredient by ID
    public Optional<FoodStock> getIngredientById(Long id) {
        return foodStockRepo.findById(id);
    }

    // Get all ingredients
    public List<FoodStock> getAllIngredients() {
        return foodStockRepo.findAll();
    }

    // Update an existing ingredient
    public FoodStock updateIngredient(Long id, FoodStock updatedIngredient) {
        return foodStockRepo.findById(id).map(ingredient -> {
            ingredient.setName(updatedIngredient.getName());
            return foodStockRepo.save(ingredient);
        }).orElseThrow(() -> new IllegalArgumentException("Ingredient with ID " + id + " not found."));
    }

    // Delete an ingredient by ID
    public void deleteIngredient(Long id) {
        if (!foodStockRepo.existsById(id)) {
            throw new IllegalArgumentException("Ingredient with ID " + id + " not found.");
        }
        foodStockRepo.deleteById(id);
    }
}

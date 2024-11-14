package HotelManagement.foodStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        ingredient.setDepletedFlag("N");
        ingredient.setStockNumber(generateSockNumber());
        return foodStockRepo.save(ingredient);

    }

    private String generateSockNumber() {
        Integer lastNumber = foodStockRepo.findLastServiceNumber();
        int nextNumber = (lastNumber != null) ? lastNumber + 1 : 1;
        String formattedNumber = String.format("%03d", nextNumber);
        return "S" + formattedNumber;
    }

    // Get a specific ingredient by ID
    public Optional<FoodStock> getIngredientById(Long id) {
        Optional<FoodStock> ingredient = foodStockRepo.findByIdAndDeletedFlagAndExpired(id, "N", false);
        if (ingredient.isPresent()) {
            // Check if the ingredient is expired
            FoodStock foodStock = ingredient.get();
            if (foodStock.isExpired()) {
                throw new RuntimeException("The ingredient with ID " + id + " has expired.");
            }
        }
        return ingredient;
    }



    // Get all ingredients
    public List<FoodStock> getAllIngredients() {
        return foodStockRepo.findAllByDeletedFlagAndExpiredOrderByExpiryDateAsc("N", false);
    }

    // Update an existing ingredient
    public FoodStock updateIngredient(Long id, FoodStock updatedIngredient) {
        return foodStockRepo.findById(id).map(ingredient -> {
            ingredient.setName(updatedIngredient.getName());
            return foodStockRepo.save(ingredient);
        }).orElseThrow(() -> new IllegalArgumentException("Ingredient with ID " + id + " not found."));
    }
    public List<FoodStock> getItemsExpiringInRange(LocalDateTime startDate, LocalDateTime endDate) {
        return foodStockRepo.findItemsExpiringInRange(startDate, endDate);
    }
    @Scheduled(cron = "0 0 * * * ?")
    public void markExpiredItems() {
        List<FoodStock> foodStocks = foodStockRepo.findAll();
        for (FoodStock foodStock : foodStocks) {
            if (foodStock.getExpiryDate().isBefore(LocalDateTime.now()) && !foodStock.isExpired()) {
                foodStock.setExpired(true);
            }
        }
        foodStockRepo.saveAll(foodStocks); // Save the updates
    }


    // Delete an ingredient by ID
    public void deleteIngredient(Long id) {
        if (!foodStockRepo.existsById(id)) {
            throw new IllegalArgumentException("Ingredient with ID " + id + " not found.");
        }
        foodStockRepo.deleteById(id);
    }
}

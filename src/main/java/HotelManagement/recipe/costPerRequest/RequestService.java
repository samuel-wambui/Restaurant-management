package HotelManagement.recipe.costPerRequest;

import HotelManagement.costing.Costing;
import HotelManagement.costing.CostingRepo;
import HotelManagement.exemption.ResourceNotFoundException;
import HotelManagement.foodStock.FoodStock;
import HotelManagement.foodStock.FoodStockRepo;
import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.RecipeRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    @Autowired
    CostPerRequestRepo costPerRequestRepo;
    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    FoodStockRepo foodStockRepo;
    @Autowired
    SpicesAndSeasoningsRepo spiceRepo;
    @Autowired
    CostingRepo costingRepo;

    public CostPerRequest createRequestCost(CostPerRequestDto cost) {
        CostPerRequest costPerRequest = new CostPerRequest();
        costPerRequest.setRequestNumber(generateRequestNumber());

        // Step 1: Validate and fetch Recipe
        Recipe recipe = recipeRepo.findByRecipeNumberAndDeletedFlag(cost.getRecipeNumber(), "N")
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found for number: " + cost.getRecipeNumber()));
        costPerRequest.setRecipeNumber(recipe.getRecipeNumber());

        // Step 2: Validate FoodStock in Recipe
        FoodStock foodStockInRecipe = recipe.getFoodStockSet().stream()
                .filter(stock -> stock.getStockName().equals(cost.getStockName()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("FoodStock not found in the Recipe's FoodStock set"));

        // Step 3: Verify FoodStock in Repository
        List<FoodStock> foodStockList = foodStockRepo.findByStockNameAndDepletedFlagAndDeletedFlag(
                cost.getStockName(), "N", "N");
        if (foodStockList.isEmpty()) {
            throw new ResourceNotFoundException("FoodStock "+cost.getStockName() + " not found in the FoodStock entity");
        }

        Double totalQuantity = foodStockRepo.findTotalQuantityByName(cost.getStockName());
        if (totalQuantity < cost.getFoodStockQuantity()) {
            throw new RuntimeException("Requested quantity exceeds available quantity. Available quantity: " + totalQuantity);
        }

        FoodStock nearestToExpire = foodStockList.stream()
                .min(Comparator.comparing(FoodStock::getExpiryDate))
                .orElseThrow(() -> new RuntimeException("Unable to determine nearest expiry for FoodStock"));

        Costing stockQuantity = costingRepo.findByStockNumber(nearestToExpire.getStockNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Costing information not found for stock number: " + nearestToExpire.getStockNumber()));

// If the first stock can fulfill the requested quantity
        if (stockQuantity.getQuantity() >= cost.getFoodStockQuantity()) {
            updateFoodStockAndCosting(nearestToExpire, stockQuantity, cost.getFoodStockQuantity());
            costPerRequest.setFoodStockPrice(calculateFoodStockPrice(cost.getStockName(), cost.getFoodStockQuantity()));
            costPerRequest.setFoodStockNumber(nearestToExpire.getStockNumber());
            costPerRequest.setFoodStockQuantity(cost.getFoodStockQuantity());
        } else {
            // If the first stock can't fulfill the requested quantity, calculate the price for the available quantity
            Double remainingQuantity = cost.getFoodStockQuantity() - stockQuantity.getQuantity();
            Double totalPrice = calculateFoodStockPrice(cost.getStockName(), stockQuantity.getQuantity());
            System.out.println("Price of found quantity: " + totalPrice);

            // Deduct the available quantity from the first stock
            updateFoodStockAndCosting(nearestToExpire, stockQuantity, stockQuantity.getQuantity());

            // Now we need to fulfill the remaining quantity from the next available stocks
            // Sort the foodStockList based on expiry date (or your required criteria)
            foodStockList.sort(Comparator.comparing(FoodStock::getExpiryDate));

            // Loop through the remaining stocks and try to fulfill the remaining quantity
            for (int i = 1; i < foodStockList.size(); i++) {
                FoodStock nextFoodStock = foodStockList.get(i);
                Costing nextStockQuantity = costingRepo.findByStockNumber(nextFoodStock.getStockNumber())
                        .orElseThrow(() -> new ResourceNotFoundException("Costing information not found for stock number: " + nextFoodStock.getStockNumber()));

                // Check if this stock can fulfill the remaining quantity
                if (nextStockQuantity.getQuantity() >= remainingQuantity) {
                    totalPrice += calculateFoodStockPrice(cost.getStockName(), remainingQuantity);  // Add the price for the remaining quantity
                    updateFoodStockAndCosting(nextFoodStock, nextStockQuantity, remainingQuantity);
                    break;  // Exit the loop since the remaining quantity is fulfilled
                } else {
                    // If this stock cannot fulfill the remaining quantity, use it all and reduce the remaining quantity
                    totalPrice += calculateFoodStockPrice(cost.getStockName(), nextStockQuantity.getQuantity());  // Add the price of this stock
                    remainingQuantity -= nextStockQuantity.getQuantity();  // Deduct the used quantity
                    updateFoodStockAndCosting(nextFoodStock, nextStockQuantity, nextStockQuantity.getQuantity());
                }
            }

            // Set the final total price for the food stock request
            costPerRequest.setFoodStockPrice(totalPrice);
        }

        // Step 5: Validate and fetch Spice in Recipe
        SpicesAndSeasonings spiceInRecipe = recipe.getSpicesSet().stream()
                .filter(spice -> spice.getSpiceNumber().equals(cost.getSpiceNumber()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Spice not found in the Recipe's Spices set"));

        // Step 6: Verify Spice in Repository
        SpicesAndSeasonings spice = spiceRepo.findBySpiceNumber(cost.getSpiceNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Spice not found in the SpicesAndSeasonings entity"));
        costPerRequest.setSpiceNumber(spice.getSpiceNumber());

        // Save the completed request
        return costPerRequestRepo.save(costPerRequest);
    }

    // Helper method to handle FoodStock updates
    private void updateFoodStockAndCosting(FoodStock foodStock, Costing stockQuantity, Double usedQuantity) {
        Double newStockQuantity = stockQuantity.getQuantity() - usedQuantity;
        stockQuantity.setQuantity(newStockQuantity);

        if (newStockQuantity == 0) {
            foodStock.setDepletedFlag("Y");
            foodStockRepo.save(foodStock);
        }
        costingRepo.save(stockQuantity);
    }

    private Double calculateFoodStockPrice(String stockNumber, Double quantity) {
        Optional<Costing> optionalCosting = costingRepo.findByStockNumber(stockNumber);

        if (optionalCosting.isPresent()) {
            Costing costing = optionalCosting.get();

            if (costing.getUnitPrice() == null) {
                throw new IllegalStateException("Unit price is not set for FoodStock: " + stockNumber);
            }

            // Calculate the total price
            Double totalPrice = costing.getUnitPrice() * quantity;

            // Format the price to 2 decimal places using BigDecimal for precision
            return Math.round(totalPrice * 100.0) / 100.0;
        } else {
            throw new ResourceNotFoundException("Costing not found for FoodStock: " + stockNumber);
        }
    }





    private String generateRequestNumber() {
            Integer lastNumber = costPerRequestRepo.findLastServiceNumber();
            int nextNumber = (lastNumber != null) ? lastNumber + 1 : 1;
            String formattedNumber = String.format("%03d", nextNumber);
            return "REQ" + formattedNumber;
        }
    }


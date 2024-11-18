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

            Optional<Recipe> optionalRecipe = recipeRepo.findByRecipeNumberAndDeletedFlag(cost.getRecipeNumber(), "N");

            if (optionalRecipe.isPresent()) {
                Recipe recipe = optionalRecipe.get();
                costPerRequest.setRecipeNumber(recipe.getRecipeNumber());

                // Step 2: Check for FoodStock in Recipe
                Optional<FoodStock> optionalFoodStockInRecipe = recipe.getFoodStockSet().stream()
                        .filter(stock -> stock.getStockNumber().equals(cost.getFoodStockNumber()))
                        .findFirst();

                if (optionalFoodStockInRecipe.isPresent()) {
                    FoodStock foodStockInRecipe = optionalFoodStockInRecipe.get();

                    // Step 3: Verify FoodStock in the Repository
                    Optional<FoodStock> optionalFoodStock = foodStockRepo.findByStockNumber(cost.getFoodStockNumber());
                    if (optionalFoodStock.isPresent()) {
                        FoodStock foodStock = optionalFoodStock.get();

                        // Step 4: Set FoodStock details in costPerRequest
                        costPerRequest.setFoodStockNumber(foodStock.getStockNumber());
                        costPerRequest.setFoodStockPrice(calculateFoodStockPrice(foodStock.getStockNumber(), cost.getFoodStockQuantity()));
                        costPerRequest.setFoodStockQuantity(cost.getFoodStockQuantity());
                    } else {
                        throw new ResourceNotFoundException("FoodStock not found in the FoodStock entity");
                    }
                } else {
                    throw new ResourceNotFoundException("FoodStock not found in the Recipe's FoodStock set");
                }

                // Step 5: Check for Spice in Recipe
                Optional<SpicesAndSeasonings> optionalSpiceInRecipe = recipe.getSpicesSet().stream()
                        .filter(spice -> spice.getSpiceNumber().equals(cost.getSpiceNumber()))
                        .findFirst();

                if (optionalSpiceInRecipe.isPresent()) {
                    SpicesAndSeasonings spiceInRecipe = optionalSpiceInRecipe.get();

                    // Step 6: Verify Spice in the Repository
                    Optional<SpicesAndSeasonings> optionalSpice = spiceRepo.findBySpiceNumber(cost.getSpiceNumber());
                    if (optionalSpice.isPresent()) {
                        SpicesAndSeasonings spice = optionalSpice.get();

                        costPerRequest.setSpiceNumber(spice.getSpiceNumber());

                    } else {
                        throw new ResourceNotFoundException("Spice not found in the SpicesAndSeasonings entity");
                    }
                } else {
                    throw new ResourceNotFoundException("Spice not found in the Recipe's Spices set");
                }
            } else {
                throw new ResourceNotFoundException("Recipe not found");
            }
            return costPerRequestRepo.save(costPerRequest);
        }

    private String calculateFoodStockPrice(String stockNumber, Double quantity) {
        Optional<Costing> optionalCosting = costingRepo.findByStockNumber(stockNumber);

        if (optionalCosting.isPresent()) {
            Costing costing = optionalCosting.get();

            if (costing.getUnitPrice() == null) {
                throw new IllegalStateException("Unit price is not set for FoodStock: " + stockNumber);
            }

            Double totalPrice = costing.getUnitPrice() * quantity;
            return String.format("%.2f", totalPrice); // Format the price to 2 decimal places
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


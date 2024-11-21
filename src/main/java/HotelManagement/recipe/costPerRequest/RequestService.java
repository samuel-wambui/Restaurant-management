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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
@Transactional
    public CostPerRequest createRequestCost(CostPerRequestDto cost) {
    CostPerRequest costPerRequest = new CostPerRequest();
    costPerRequest.setRequestNumber(generateRequestNumber());

    // Step 1: Validate and fetch Recipe
    Recipe recipe = recipeRepo.findByRecipeNumberAndDeletedFlag(cost.getRecipeNumber(), "N")
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found for number: " + cost.getRecipeNumber()));
    costPerRequest.setRecipeNumber(recipe.getRecipeNumber());

    // Step 2: Validate FoodStock in Recipe
    Set<String> foodStockNames = recipe.getFoodStockSet().stream()
            .map(FoodStock::getStockName) // Convert each FoodStock object to its stockName
            .collect(Collectors.toSet()); // Collect names into a Set for easy lookup
    System.out.println("these are the food stock names in the recipe: " + foodStockNames);

// Step 2: Check if the requested stock name exists in the Recipe's FoodStock names
    if (!foodStockNames.contains(cost.getStockName())) {
        throw new ResourceNotFoundException("FoodStock with name '" + cost.getStockName() +
                "' not found in the Recipe's FoodStock set");
    }

// Step 3: Retrieve the specific FoodStock object (optional, if needed later)
    FoodStock foodStockInRecipe = recipe.getFoodStockSet().stream()
            .filter(stock -> stock.getStockName().equals(cost.getStockName()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("FoodStock not found in the Recipe's FoodStock set"));
    // Step 3: Verify FoodStock in Repository
    List<FoodStock> foodStockList = foodStockRepo.findByStockNameAndDepletedFlagAndDeletedFlag(
            cost.getStockName(), "N", "N");
    if (foodStockList.isEmpty()) {
        throw new ResourceNotFoundException("FoodStock " + cost.getStockName() + " not found in the FoodStock entity");
    }

    Double totalQuantity = foodStockRepo.findTotalQuantityByName(cost.getStockName());
    if (totalQuantity < cost.getFoodStockQuantity()) {
        throw new RuntimeException("Requested quantity exceeds available quantity. Available quantity: " + totalQuantity);
    }

    FoodStock nearestToExpire = foodStockList.stream()
            .min(Comparator.comparing(FoodStock::getExpiryDate))
            .orElseThrow(() -> new RuntimeException("Unable to determine nearest expiry for FoodStock"));
    System.out.println("nearestToExpire: " + nearestToExpire.getStockNumber());

    Costing stockQuantity = costingRepo.findByStockNumber(nearestToExpire.getStockNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Costing information not found for stock number: " + nearestToExpire.getStockNumber()));

// If the first stock can fulfill the requested quantity
    if (stockQuantity.getQuantity() >= cost.getFoodStockQuantity()) {
        // If the first stock can fulfill the requested quantity
        updateFoodStockAndCosting(nearestToExpire, stockQuantity, cost.getFoodStockQuantity());

        costPerRequest.setFoodStockPrice(calculateFoodStockPrice(cost.getStockName(), cost.getFoodStockQuantity()));
        costPerRequest.setFoodStockNumber(nearestToExpire.getStockName());
        costPerRequest.setFoodStockQuantity(cost.getFoodStockQuantity());
        return costPerRequestRepo.save(costPerRequest);

    } else {
        // If the first stock can't fulfill the requested quantity
        Double remainingQuantity = cost.getFoodStockQuantity() - stockQuantity.getQuantity();
        Double totalPrice = 0.0;

        // Deduct the available quantity from the first stock and calculate its price
        totalPrice += calculateFoodStockPrice(nearestToExpire.getStockNumber(), stockQuantity.getQuantity());
        updateFoodStockAndCosting(nearestToExpire, stockQuantity, stockQuantity.getQuantity());

        // Sort the remaining stocks by expiry date
        foodStockList.sort(Comparator.comparing(FoodStock::getExpiryDate));

        // Loop through remaining stocks
        for (int i = 1; i < foodStockList.size(); i++) {
            FoodStock nextFoodStock = foodStockList.get(i);
            Costing nextStockQuantity = costingRepo.findByStockNumber(nextFoodStock.getStockNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Costing information not found for stock number: " + nextFoodStock.getStockNumber()));

            if (remainingQuantity <= nextStockQuantity.getQuantity()) {
                // This stock can fulfill the remaining quantity
                totalPrice += calculateFoodStockPrice(nextFoodStock.getStockNumber(), remainingQuantity);
                System.out.println("totalPrice second for foodstock: " +totalPrice);
                updateFoodStockAndCosting(nextFoodStock, nextStockQuantity, remainingQuantity);
                remainingQuantity = 0.0; // Remaining quantity is fully satisfied
                break;
            } else {
                // Use all of this stock's quantity
                totalPrice += calculateFoodStockPrice(nextFoodStock.getStockNumber(), nextStockQuantity.getQuantity());
                System.out.println("totalPrice for foodstock: " +totalPrice);
                remainingQuantity -= nextStockQuantity.getQuantity();
                updateFoodStockAndCosting(nextFoodStock, nextStockQuantity, nextStockQuantity.getQuantity());
            }
        }


        // Set the final total price and other details
        costPerRequest.setFoodStockPrice(totalPrice);
        costPerRequest.setStockName(nearestToExpire.getStockName() );
        costPerRequest.setFoodStockQuantity(cost.getFoodStockQuantity());
        costPerRequestRepo.save(costPerRequest);
    }

    // Step 5: Validate and fetch Spice in Recipe
    // Step 5: Validate and fetch Spice in Recipe
    Optional<SpicesAndSeasonings> optionalSpiceInRecipe = recipe.getSpicesSet().stream()
            .filter(spice -> spice.getSpiceNumber().equals(cost.getSpiceNumber()))
            .findFirst();

    if (optionalSpiceInRecipe.isPresent()) {
        SpicesAndSeasonings spiceInRecipe = optionalSpiceInRecipe.get();

        // Step 6: Verify Spice in Repository
        Optional<SpicesAndSeasonings> optionalSpice = spiceRepo.findBySpiceNumber(cost.getSpiceNumber());
        if (optionalSpice.isPresent()) {
            SpicesAndSeasonings spice = optionalSpice.get();
            costPerRequest.setSpiceNumber(spice.getSpiceNumber());
        } else {
            System.out.println("Spice not found in the SpicesAndSeasonings entity for number: " + cost.getSpiceNumber());
            costPerRequest.setSpiceNumber(null); // Or set a default value if needed
        }
    } else {
        System.out.println("Spice not found in the Recipe's Spices set for number: " + cost.getSpiceNumber());
        costPerRequest.setSpiceNumber(null); // Or set a default value if needed
    }

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
        Double unitPrice;

        if (optionalCosting.isPresent()) {
            Costing costing = optionalCosting.get();

            // Handle the case where unitPrice is null
            if (costing.getUnitPrice() == null) {
                System.out.println("Unit price is not set for FoodStock: " + stockNumber);
                unitPrice = 0.0; // Set unit price to default 0.0
            } else {
                unitPrice = costing.getUnitPrice();
            }
        } else {
            System.out.println("Costing not found for FoodStock: " + stockNumber);
            unitPrice = 0.0; // Set default unit price if costing is not found
        }

        // Calculate the total price using the resolved unit price
        Double totalPrice = unitPrice * quantity;

        // Format the price to 2 decimal places
        return Math.round(totalPrice * 100.0) / 100.0;
    }


    private String generateRequestNumber() {
            Integer lastNumber = costPerRequestRepo.findLastServiceNumber();
            int nextNumber = (lastNumber != null) ? lastNumber + 1 : 1;
            String formattedNumber = String.format("%03d", nextNumber);
            return "REQ" + formattedNumber;
        }
    }


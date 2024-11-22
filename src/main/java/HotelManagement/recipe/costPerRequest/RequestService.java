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
    System.out.println("Starting createRequestCost for Recipe Number: " + cost.getRecipeNumber());

    // Step 1: Initialize CostPerRequest and assign a unique request number
    CostPerRequest costPerRequest = new CostPerRequest();
    costPerRequest.setRequestNumber(generateRequestNumber());
    System.out.println("Generated Request Number: " + costPerRequest.getRequestNumber());

    // Step 2: Validate and fetch the Recipe
    System.out.println("Fetching Recipe for Recipe Number: " + cost.getRecipeNumber());
    Recipe recipe = recipeRepo.findByRecipeNumberAndDeletedFlag(cost.getRecipeNumber(), "N")
            .orElseThrow(() -> {
                System.out.println("Recipe not found for Recipe Number: " + cost.getRecipeNumber());
                return new ResourceNotFoundException("Recipe not found for number: " + cost.getRecipeNumber());
            });
    costPerRequest.setRecipeNumber(recipe.getRecipeNumber());
    System.out.println("Recipe fetched successfully: " + recipe);

    // Step 3: Initialize total cost and iterate through FoodStock requests
    double totalFoodStockPrice = 0.0;

    for (FoodStockRequestDto foodStockDto : cost.getFoodStocks()) {
        System.out.println("Processing FoodStock with ID: " + foodStockDto.getId() + ", Requested Quantity: " + foodStockDto.getFoodStockQuantity());

        Long id = foodStockDto.getId();
        double requestedQuantity = foodStockDto.getFoodStockQuantity();

        Optional<FoodStock> optionalFoodStock = foodStockRepo.findByIdAndDeletedFlagAndExpired(id, "N", false);
        if (!optionalFoodStock.isPresent()) {
            System.out.println("FoodStock not found or invalid for ID: " + id);
            throw new ResourceNotFoundException("FoodStock with ID " + id + " not found or invalid.");
        }

        FoodStock foodStock = optionalFoodStock.get();
        String stockName = foodStock.getStockName();

        // Fetch all valid stocks with the same name
        List<FoodStock> foodStockList = foodStockRepo.findByStockNameAndDepletedFlagAndDeletedFlag(stockName, "N", "N");
        if (foodStockList.isEmpty()) {
            System.out.println("No valid FoodStocks found for name: " + stockName);
            throw new ResourceNotFoundException("No valid FoodStocks found for " + stockName);
        }

        System.out.println("Valid FoodStock List: " + foodStockList);

        double totalAvailableQuantity = foodStockRepo.findTotalQuantityByName(stockName);
        if (totalAvailableQuantity < requestedQuantity) {
            System.out.println("Requested quantity exceeds available quantity for " + stockName);
            throw new RuntimeException("Requested quantity exceeds available quantity for " + stockName
                    + ". Available quantity: " + totalAvailableQuantity);
        }

        // Sort FoodStocks by expiry date and calculate cost
        double remainingQuantity = requestedQuantity;
        double foodStockPrice = 0.0;

        foodStockList.sort(Comparator.comparing(FoodStock::getExpiryDate));
        System.out.println("Sorted FoodStock List by Expiry Date: " + foodStockList);

        for (FoodStock stock : foodStockList) {
            System.out.println("Processing Stock: " + stock.getStockNumber() + " " + stock.getStockName());

            // Fetch costing details for the current stock
            Costing stockCosting = costingRepo.findByStockNumber(stock.getStockNumber())
                    .orElseThrow(() -> {
                        System.out.println("Costing not found for stock number: " + stock.getStockNumber());
                        return new ResourceNotFoundException("Costing not found for stock number: " + stock.getStockNumber());
                    });

            System.out.println("Fetched Costing: " + stockCosting);

            if (remainingQuantity <= stockCosting.getQuantity()) {
                // This stock can fulfill the remaining quantity
                double currentStockCost = calculateFoodStockPrice(stock.getStockNumber(), remainingQuantity);
                foodStockPrice += currentStockCost;

                System.out.println("Processed FoodStock: " + stock.getStockName() + ", Used Quantity: " + remainingQuantity + ", Cost: " + currentStockCost);
                updateFoodStockAndCosting(stock, stockCosting, remainingQuantity);
                remainingQuantity = 0.0;

                // Terminate the loop for this food stock as the requested amount is fulfilled
                break;
            } else {
                // Use all of this stock's quantity
                double currentStockCost = calculateFoodStockPrice(stock.getStockNumber(), stockCosting.getQuantity());
                foodStockPrice += currentStockCost;

                System.out.println("Processed FoodStock: " + stock.getStockName() + ", Used Quantity: " + stockCosting.getQuantity() + ", Cost: " + currentStockCost);
                remainingQuantity -= stockCosting.getQuantity();
                updateFoodStockAndCosting(stock, stockCosting, stockCosting.getQuantity());
            }
        }

        System.out.println("Total cost for FoodStock '" + stockName + "': " + foodStockPrice);
        totalFoodStockPrice += foodStockPrice;
    }

    // Step 4: Set final details in CostPerRequest
    costPerRequest.setFoodStockPrice(totalFoodStockPrice);
    costPerRequest.setFoodStockQuantity(cost.getFoodStocks().stream().mapToDouble(FoodStockRequestDto::getFoodStockQuantity).sum());

    System.out.println("Final total cost for all FoodStocks in Recipe: " + totalFoodStockPrice);

    // Save and return the CostPerRequest object
    costPerRequestRepo.save(costPerRequest);
    System.out.println("CostPerRequest saved successfully: " + costPerRequest);



    //





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


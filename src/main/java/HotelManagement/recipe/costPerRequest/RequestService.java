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

import java.util.*;
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

    // Step 1: Initialize CostPerRequest
    CostPerRequest costPerRequest = initializeCostPerRequest();

    // Step 2: Validate and fetch the Recipe
    Recipe recipe = fetchRecipe(cost.getRecipeNumber());
    costPerRequest.setRecipeNumber(recipe.getRecipeNumber());

    // Step 3: Process Food Stocks
    processFoodStocks(cost, recipe, costPerRequest);

    // Step 4: Process Spices
    processSpices(cost, recipe, costPerRequest);

    // Save and return the completed CostPerRequest
    return costPerRequestRepo.save(costPerRequest);
}

// Helper Methods

    private CostPerRequest initializeCostPerRequest() {
        CostPerRequest costPerRequest = new CostPerRequest();
        costPerRequest.setRequestNumber(generateRequestNumber());
        System.out.println("Generated Request Number: " + costPerRequest.getRequestNumber());
        return costPerRequest;
    }

    private Recipe fetchRecipe(String recipeNumber) {
        System.out.println("Fetching Recipe for Recipe Number: " + recipeNumber);
        return recipeRepo.findByRecipeNumberAndDeletedFlag(recipeNumber, "N")
                .orElseThrow(() -> {
                    System.out.println("Recipe not found for Recipe Number: " + recipeNumber);
                    return new ResourceNotFoundException("Recipe not found for number: " + recipeNumber);
                });
    }

    private void processFoodStocks(CostPerRequestDto cost, Recipe recipe, CostPerRequest costPerRequest) {
        double totalFoodStockPrice = 0.0;

        // Get distinct stock names and numbers
        Set<String> stockNames = getDistinctStockNames(recipe);
        Set<String> foodStockNumbers = getDistinctFoodStockNumbers(recipe, stockNames);

        System.out.println("Distinct stock names: " + stockNames);
        System.out.println("Distinct food stock numbers: " + foodStockNumbers);

        for (FoodStockRequestDto foodStockDto : cost.getFoodStocks()) {
            double foodStockPrice = processSingleFoodStock(foodStockDto);
            totalFoodStockPrice += foodStockPrice;
        }

        costPerRequest.setFoodStockPrice(totalFoodStockPrice);
        costPerRequest.setFoodStockQuantity(cost.getFoodStocks().stream()
                .mapToDouble(FoodStockRequestDto::getFoodStockQuantity).sum());
        costPerRequest.setStockName(stockNames.toString());
        costPerRequest.setFoodStockNumber(foodStockNumbers.toString());

        System.out.println("Final total cost for all FoodStocks: " + totalFoodStockPrice);
    }

    private Set<String> getDistinctStockNames(Recipe recipe) {
        return recipe.getFoodStockSet().stream()
                .map(FoodStock::getStockName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> getDistinctFoodStockNumbers(Recipe recipe, Set<String> stockNames) {
        return stockNames.stream()
                .flatMap(stockName -> recipe.getFoodStockSet().stream()
                        .filter(foodStock -> foodStock.getStockName().equals(stockName))
                        .map(FoodStock::getStockNumber))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private double processSingleFoodStock(FoodStockRequestDto foodStockDto) {
        Long id = foodStockDto.getId();
        double requestedQuantity = foodStockDto.getFoodStockQuantity();
        double totalCost = 0.0;

        FoodStock foodStock = foodStockRepo.findByIdAndDeletedFlagAndExpired(id, "N", false)
                .orElseThrow(() -> new ResourceNotFoundException("FoodStock with ID " + id + " not found or invalid."));
        List<FoodStock> validStocks = foodStockRepo.findValidFoodStocks(foodStock.getStockName());
        double totalAvailableQuantity = foodStockRepo.findTotalQuantityByName(foodStock.getStockName());

        if (totalAvailableQuantity < requestedQuantity) {
            throw new RuntimeException("Requested quantity exceeds available quantity for " + foodStock.getStockName()+" is " + totalAvailableQuantity );
        }

        double remainingQuantity = requestedQuantity;
        for (FoodStock stock : validStocks) {
            Costing costing = costingRepo.findByStockNumber(stock.getStockNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Costing not found for stock number: " + stock.getStockNumber()));

            if (remainingQuantity <= costing.getQuantity()) {
                totalCost += calculateFoodStockPrice(stock.getStockNumber(), remainingQuantity);
                updateFoodStockAndCosting(stock, costing, remainingQuantity);
                break;
            } else {
                totalCost += calculateFoodStockPrice(stock.getStockNumber(), costing.getQuantity());
                remainingQuantity -= costing.getQuantity();
                updateFoodStockAndCosting(stock, costing, costing.getQuantity());
            }
        }

        return totalCost;
    }

    private void processSpices(CostPerRequestDto cost, Recipe recipe, CostPerRequest costPerRequest) {
        recipe.getSpicesSet().stream()
                .filter(spice -> spice.getSpiceNumber().equals(cost.getSpiceNumber()))
                .findFirst()
                .ifPresentOrElse(spiceInRecipe -> {
                    spiceRepo.findBySpiceNumber(cost.getSpiceNumber())
                            .ifPresentOrElse(spice -> costPerRequest.setSpiceNumber(spice.getSpiceNumber()),
                                    () -> {
                                        System.out.println("Spice not found for number: " + cost.getSpiceNumber());
                                        costPerRequest.setSpiceNumber(null);
                                    });
                }, () -> {
                    System.out.println("Spice not found in Recipe for number: " + cost.getSpiceNumber());
                    costPerRequest.setSpiceNumber(null);
                });
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


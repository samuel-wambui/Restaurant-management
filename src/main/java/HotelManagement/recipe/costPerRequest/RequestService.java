package HotelManagement.recipe.costPerRequest;

import HotelManagement.costing.Costing;
import HotelManagement.costing.CostingRepo;
import HotelManagement.exemption.ResourceNotFoundException;
import HotelManagement.foodStock.FoodStock;
import HotelManagement.foodStock.FoodStockRepo;
import HotelManagement.foodStock.unitMeasurement.UnitMeasurement;
import HotelManagement.foodStock.unitMeasurement.UnitMeasurementRepo;
import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.RecipeRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    UnitMeasurementRepo unitMeasurementRepo;
@Transactional
public CostPerRequest createRequestCost(CostPerRequestDto cost) {
    System.out.println("Starting createRequestCost for Recipe Number: " + cost.getRecipeNumber());

    // Step 1: Initialize CostPerRequest
    CostPerRequest costPerRequest = initializeCostPerRequest();

    // Step 2: Validate and fetch the Recipe
    Recipe recipe = fetchRecipe(cost.getRecipeNumber());
    costPerRequest.setRecipeNumber(recipe.getRecipeNumber());

    // Step 3: Process Food Stocks and calculate their total cost
    processFoodStocks(cost, recipe, costPerRequest);

    // Step 4: Process Spices and assign to CostPerRequest
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
        BigDecimal totalFoodStockPrice = BigDecimal.ZERO;
        Map<Long, BigDecimal> convertedQuantitiesMap = new HashMap<>();
        LinkedHashMap<String, BigDecimal> stockNameToQuantityMap = initializeStockNameToQuantityMap(recipe);

        for (FoodStockRequestDto foodStockDto : cost.getFoodStocks()) {
            BigDecimal convertedQuantity = generateQuantity(foodStockDto, recipe);
            convertedQuantitiesMap.put(foodStockDto.getId(), convertedQuantity);

            FoodStock foodStock = fetchFoodStock(foodStockDto.getId());
            stockNameToQuantityMap.merge(foodStock.getStockName(), convertedQuantity, BigDecimal::add);

            BigDecimal foodStockPrice = processSingleFoodStock(foodStockDto, recipe, convertedQuantity);
            totalFoodStockPrice = totalFoodStockPrice.add(foodStockPrice);
        }

        costPerRequest.setFoodStockPrice(totalFoodStockPrice.doubleValue());
        costPerRequest.setFoodStockQuantity(formatQuantitiesInOrder(stockNameToQuantityMap));
        costPerRequest.setStockName(String.join(", ", stockNameToQuantityMap.keySet()));

        System.out.println("Total FoodStock Price: " + totalFoodStockPrice);
        System.out.println("Quantities in Recipe Order: " + stockNameToQuantityMap);
    }
    private String formatQuantitiesInOrder(Map<String, BigDecimal> stockNameToQuantityMap) {
        return stockNameToQuantityMap.values().stream()
                .map(BigDecimal::toPlainString) // Converts BigDecimal to plain string representation
                .collect(Collectors.joining(", ")); // Joins the strings with a comma
    }

    private LinkedHashMap<String, BigDecimal> initializeStockNameToQuantityMap(Recipe recipe) {
        LinkedHashMap<String, BigDecimal> map = new LinkedHashMap<>();
        recipe.getFoodStockSet().forEach(stock -> map.put(stock.getStockName(), BigDecimal.ZERO));
        return map;
    }

    private FoodStock fetchFoodStock(Long id) {
        return foodStockRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodStock not found with ID: " + id));
    }

    private BigDecimal processSingleFoodStock(FoodStockRequestDto foodStockDto, Recipe recipe, BigDecimal requestedQuantity) {
        Long id = foodStockDto.getId();
        BigDecimal totalCost = BigDecimal.ZERO;

        FoodStock foodStock = foodStockRepo.findByIdAndDeletedFlagAndExpired(id, "N", false)
                .orElseThrow(() -> new ResourceNotFoundException("FoodStock with ID " + id + " not found or invalid."));
        List<FoodStock> validStocks = fetchValidFoodStocks(foodStock.getStockName());
        BigDecimal totalAvailableQuantity = fetchTotalAvailableQuantity(foodStock.getStockName());

        if (totalAvailableQuantity.compareTo(requestedQuantity) < 0) {
            throw new RuntimeException("Requested quantity exceeds available quantity for " +
                    foodStock.getStockName() + ". Available: " + totalAvailableQuantity);
        }

        BigDecimal remainingQuantity = requestedQuantity;
        for (FoodStock stock : validStocks) {
            Costing costing = fetchCosting(stock.getStockNumber());
            BigDecimal costingQuantity = BigDecimal.valueOf(costing.getQuantity());

            if (remainingQuantity.compareTo(costingQuantity) <= 0) {
                totalCost = totalCost.add(calculateFoodStockPrice(stock.getStockNumber(), remainingQuantity));
                updateFoodStockAndCosting(stock, costing, remainingQuantity);
                break;
            } else {
                totalCost = totalCost.add(calculateFoodStockPrice(stock.getStockNumber(), costingQuantity));
                remainingQuantity = remainingQuantity.subtract(costingQuantity);
                updateFoodStockAndCosting(stock, costing, costingQuantity);
            }
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }
    private List<FoodStock> fetchValidFoodStocks(String stockName) {
        return foodStockRepo.findValidFoodStocks(stockName);  // Assuming `findValidFoodStocks` is defined in the repo
    }
    private BigDecimal fetchTotalAvailableQuantity(String stockName) {
        // This assumes you have a method in your foodStockRepo to fetch the total quantity by stock name
        return BigDecimal.valueOf(foodStockRepo.findTotalQuantityByName(stockName));
    }


    private BigDecimal calculateFoodStockPrice(String stockNumber, BigDecimal quantity) {
        Costing costing = fetchCosting(stockNumber);
        BigDecimal unitPrice = BigDecimal.valueOf(costing.getUnitPrice() == null ? 0.0 : costing.getUnitPrice());
        return unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }
    private Costing fetchCosting(String stockNumber) {
        return costingRepo.findByStockNumber(stockNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Costing not found for stock number: " + stockNumber));
    }

    private void updateFoodStockAndCosting(FoodStock foodStock, Costing costing, BigDecimal usedQuantity) {
        BigDecimal newStockQuantity = BigDecimal.valueOf(costing.getQuantity()).subtract(usedQuantity);
        costing.setQuantity(newStockQuantity.doubleValue());

        if (newStockQuantity.compareTo(BigDecimal.ZERO) == 0) {
            foodStock.setDepletedFlag("Y");
            foodStockRepo.save(foodStock);
        }
        costingRepo.save(costing);
    }

    private BigDecimal generateQuantity(FoodStockRequestDto requestDto, Recipe recipe) {
        String unitNumber = recipe.getFoodStockSet().stream()
                .map(FoodStock::getUnitNumber)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Unit number not found in recipe."));

        UnitMeasurement unitMeasurement = unitMeasurementRepo.findByUnitMeasurementNumber(unitNumber);
        if (unitMeasurement == null) {
            throw new ResourceNotFoundException("Unit measurement not found for unit number: " + unitNumber);
        }

        BigDecimal quantity = BigDecimal.valueOf(requestDto.getFoodStockQuantity());
        if (requestDto.getMeasurementCategory() == MeasurementCategory.Sub_unit) {
            BigDecimal subUnitValue = BigDecimal.valueOf(unitMeasurement.getSubUnit());
            if (subUnitValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid sub-unit value: " + unitMeasurement.getSubUnit());
            }
            quantity = quantity.divide(subUnitValue, 5, RoundingMode.HALF_UP);
        }

        return quantity;
    }

    private void processSpices(CostPerRequestDto cost, Recipe recipe, CostPerRequest costPerRequest) {
        recipe.getSpicesSet().stream()
                .filter(spice -> spice.getSpiceNumber().equals(cost.getSpiceNumber()))
                .findFirst()
                .ifPresentOrElse(spiceInRecipe -> {
                    spiceRepo.findBySpiceNumber(cost.getSpiceNumber())
                            .ifPresentOrElse(spice -> costPerRequest.setSpiceNumber(spice.getSpiceNumber()),
                                    () -> costPerRequest.setSpiceNumber(null));
                }, () -> costPerRequest.setSpiceNumber(null));
    }

    private String generateRequestNumber() {
            Integer lastNumber = costPerRequestRepo.findLastServiceNumber();
            int nextNumber = (lastNumber != null) ? lastNumber + 1 : 1;
            String formattedNumber = String.format("%03d", nextNumber);
            return "REQ" + formattedNumber;
        }
    }


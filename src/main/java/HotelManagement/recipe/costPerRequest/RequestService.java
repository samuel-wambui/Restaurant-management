package HotelManagement.recipe.costPerRequest;

import HotelManagement.costing.Costing;
import HotelManagement.costing.CostingRepo;
import HotelManagement.exemption.ResourceNotFoundException;
import HotelManagement.stock.foodStock.FoodStock;
import HotelManagement.stock.foodStock.FoodStockRepo;
import HotelManagement.stock.foodStock.unitMeasurement.UnitMeasurement;
import HotelManagement.stock.foodStock.unitMeasurement.UnitMeasurementRepo;
import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.RecipeRepo;
import HotelManagement.stock.foodStock.spices.SpicesAndSeasoningsRepo;
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
        costPerRequest.setRequestNumber(generateRequestNumber());

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

        BigDecimal totalCost = BigDecimal.ZERO;

        // Fetch the FoodStock entity if it exists and is valid


        FoodStock foodStock = validatedFoodStock(recipe,foodStockDto);
        List<FoodStock> validStocks = fetchValidFoodStocks(foodStock.getStockName());
        BigDecimal totalAvailableQuantity = fetchTotalAvailableQuantity(foodStock.getStockName());

        // Ensure the requested quantity does not exceed the available quantity
        if (totalAvailableQuantity.compareTo(requestedQuantity) < 0) {
            throw new RuntimeException("Requested quantity exceeds available quantity for " +
                    foodStock.getStockName() + ". Available: " + totalAvailableQuantity);
        }

        BigDecimal remainingQuantity = requestedQuantity;

        // Process each valid stock to fulfill the requested quantity
        for (FoodStock stock : validStocks) {
            Costing costing = fetchCosting(stock.getStockNumber());
            BigDecimal costingQuantity = BigDecimal.valueOf(costing.getQuantity());

            if (remainingQuantity.compareTo(costingQuantity) <= 0) {
                // Fully fulfill the remaining quantity from the current stock
                totalCost = totalCost.add(calculateFoodStockPrice(stock.getStockNumber(), remainingQuantity));
                updateFoodStockAndCosting(stock, costing, remainingQuantity);
                break;
            } else {
                // Partially fulfill the quantity and continue with the next stock
                totalCost = totalCost.add(calculateFoodStockPrice(stock.getStockNumber(), costingQuantity));
                remainingQuantity = remainingQuantity.subtract(costingQuantity);
                updateFoodStockAndCosting(stock, costing, costingQuantity);
            }
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    public FoodStock validatedFoodStock(Recipe recipe, FoodStockRequestDto foodStockDto) {
        // Extract the food stock ID from the request DTO
        Long id = foodStockDto.getId();
        System.out.println("Validating FoodStock with ID: " + id);

        // Retrieve the FoodStock object from the repository
        Optional<FoodStock> optionalFoodStock = foodStockRepo.findByIdAndDeletedFlagAndExpiredAndDepletedFlag(id, "N", false);
        if (optionalFoodStock.isPresent()){
            FoodStock foodStock = optionalFoodStock.get();
        System.out.println("FoodStock retrieval result: " + foodStock.getStockName());

        // If the FoodStock does not exist or is invalid, throw a ResourceNotFoundException
        if (optionalFoodStock.isEmpty()) {
            String errorMessage = "FoodStock with ID " + id + " not found, invalid, or depleted.";
            System.out.println(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }}

        // Retrieve the validated FoodStock from the Optional
        FoodStock foodStock = optionalFoodStock.get();
        System.out.println("Validated FoodStock: " + foodStock.getStockName());

        // Compare the Recipe's foodStock set with the incoming request's FoodStock
        Set<String> recipeFoodStockNames = recipe.getFoodStockSet()

                .stream()
                .map(FoodStock::getStockName)
                .collect(Collectors.toSet());
        //System.out.println("found ids " + recipeFoodStockIds );

        if (!recipeFoodStockNames.contains(foodStock.getStockName())) {
            // Find the unmatched FoodStock(s)
            String unmatchedFoodStocks = recipe.getFoodStockSet()
                    .stream()
                    .map(FoodStock::getStockName)
                    .collect(Collectors.joining(", "));

            String errorMessage = "FoodStock " + foodStock.getStockName() + " (ID: " + foodStock.getId() + ") is not part of the recipe. " +
                    "Valid Recipe FoodStocks: " + unmatchedFoodStocks;
            System.out.println(errorMessage);

            throw new IllegalArgumentException(errorMessage);
        }

        // Return the validated FoodStock object if it's part of the recipe
        return foodStock;
    }



    private List<FoodStock> fetchValidFoodStocks(String stockName) {
        return foodStockRepo.findValidFoodStocks(stockName);  // Assuming findValidFoodStocks is defined in the repo
    }
    private BigDecimal fetchTotalAvailableQuantity(String stockName) {
        // This assumes you have a method in your foodStockRepo to fetch the total quantity by stock name
        return BigDecimal.valueOf(foodStockRepo.findTotalQuantityByName(stockName));
    }


    private BigDecimal calculateFoodStockPrice(String stockNumber, BigDecimal quantity) {
        // Fetch the Costing object using the stockNumber
        Optional<Costing> optionalCosting = costingRepo.findByStockNumber(stockNumber);

        // Check if the Costing object exists
        if (optionalCosting.isPresent()) {
            Costing costing = optionalCosting.get();

            // Log the unitPrice and quantity
            System.out.println("Unit Price: " + costing.getUnitPrice());
            System.out.println("Quantity: " + quantity);

            // If unit price is null, throw an exception
            if (costing.getUnitPrice() == null) {
                throw new IllegalStateException("Unit price is not set for FoodStock: " + stockNumber);
            }

            // Calculate the total price by multiplying the unit price with the quantity
            BigDecimal totalPrice = BigDecimal.valueOf(costing.getUnitPrice())
                    .multiply(quantity);

            // Log the totalPrice before rounding
            System.out.println("Total Price before rounding: " + totalPrice);

            // Round the total price to 2 decimal places
            return totalPrice.setScale(2, RoundingMode.HALF_UP);
        } else {
            // If no Costing is found, throw an exception
            throw new ResourceNotFoundException("Costing not found for FoodStock: " + stockNumber);
        }
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


    @Transactional
    public CostPerRequest updateRequestCost(Long id, CostPerRequestDto updatedCost) {
        // Fetch existing CostPerRequest
        CostPerRequest existingCost = costPerRequestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CostPerRequest not found with ID: " + id));

        System.out.println("Updating CostPerRequest ID: " + id);

        // Update Recipe Number if applicable
        if (updatedCost.getRecipeNumber() != null) {
            Recipe recipe = fetchRecipe(updatedCost.getRecipeNumber());
            existingCost.setRecipeNumber(recipe.getRecipeNumber());
        }

        // Update Food Stock costs and quantities
        if (updatedCost.getFoodStocks() != null) {
            processFoodStocks(updatedCost, fetchRecipe(existingCost.getRecipeNumber()), existingCost);
        }

        // Update Spices
        if (updatedCost.getSpiceNumber() != null) {
            processSpices(updatedCost, fetchRecipe(existingCost.getRecipeNumber()), existingCost);
        }

        // Save updated entity
        return costPerRequestRepo.save(existingCost);
    }
    public CostPerRequest getRequestCostById(Long id) {
        return costPerRequestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CostPerRequest not found with ID: " + id));
    }
    public List<CostPerRequest> getAllRequestCosts() {
        return costPerRequestRepo.findAll();
    }
    public CostPerRequest deleteCostPerRequest(Long id) {
        Optional<CostPerRequest> optionalCostPerRequest = costPerRequestRepo.findById(id);
        if (optionalCostPerRequest.isPresent()) {
            CostPerRequest deletedCostPerRequest = optionalCostPerRequest.get();
            deletedCostPerRequest.setDeletedFlag("Y"); // Soft delete using 'Y' flag
            return costPerRequestRepo.save(deletedCostPerRequest); // Save and return the updated entity
        } else {
            throw new ResourceNotFoundException("CostPerRequest with ID " + id + " not found");
        }
    }

}


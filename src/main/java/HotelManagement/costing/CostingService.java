package HotelManagement.costing;


import HotelManagement.exemption.ResourceNotFoundException;
import HotelManagement.foodStock.FoodStock;
import HotelManagement.foodStock.FoodStockDto;
import HotelManagement.foodStock.FoodStockRepo;
import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.RecipeRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CostingService {
    private static final Logger logger = LoggerFactory.getLogger(CostingService.class);

    @Autowired
    private CostingRepo costingRepo;

    @Autowired
    private FoodStockRepo foodStockRepo;

    @Autowired
    private SpicesAndSeasoningsRepo spicesRepo;
    @Autowired
    private RecipeRepo recipeRepo;

//    public Costing saveCost(CostingDto costingDto) {
//        // Validate input
//        validateCostingDto(costingDto);
//
//        Optional<Recipe> recipeOptional = recipeRepo.findByIdAndDeletedFlag(costingDto.getRecipeId(), "N");
//        if (recipeOptional.isEmpty()) {
//            throw new ResourceNotFoundException("Recipe with id " + costingDto.getRecipeId() + " is not found");
//        }
//
//        Recipe recipe = recipeOptional.get();
//        Costing savedCost = null;
//
//
//        // Check if cost category is INGREDIENT
//        if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
//            // Find the ingredient in the recipe
//            Optional<FoodStock> ingredientOpt = recipe.getFoodStockSet().stream()
//                    .filter(ingredient -> ingredient.getId().equals(costingDto.getCommodityId()))
//                    .findFirst();
//
//            if (ingredientOpt.isEmpty()) {
//                throw new ResourceNotFoundException("Ingredient not found in " + recipe.getRecipeName());
//            }
//
//            FoodStock ingredient = ingredientOpt.get();
//            Optional<Costing> existingCostingOpt = costingRepo.findByRecipeIdAndCommodityIdAndCostCategory(
//                    recipe.getId(), ingredient.getId(), CostCategory.INGREDIENT);
//
//            // Update or create new entry based on existence
//            Costing ingredientCost;
//            if (existingCostingOpt.isPresent()) {
//                ingredientCost = existingCostingOpt.get();
//                ingredientCost.setCost(costingDto.getCost());
//                ingredientCost.setQuantity(costingDto.getQuantity());
//            } else {
//                ingredientCost = new Costing();
//                ingredientCost.setCommodityId(ingredient.getId());
//                ingredientCost.setQuantity(costingDto.getQuantity());
//                ingredientCost.setCost(costingDto.getCost());
//                ingredientCost.setCostCategory(CostCategory.INGREDIENT);
//                ingredientCost.setRecipeId(recipe.getId());
//                ingredientCost.setDeletedFlag("N");
//            }
//
//            savedCost = costingRepo.save(ingredientCost);
//
//        } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
//            // Check if the spice exists in the recipe
//            Optional<SpicesAndSeasonings> spiceOpt = recipe.getSpicesSet().stream()
//                    .filter(spice -> spice.getId().equals(costingDto.getCommodityId()))
//                    .findFirst();
//
//            if (spiceOpt.isEmpty()) {
//                throw new ResourceNotFoundException("Spice not found in" + recipe.getRecipeName());
//            }
//
//            SpicesAndSeasonings spice = spiceOpt.get();
//            Optional<Costing> existingCostingOpt = costingRepo.findByRecipeIdAndCommodityIdAndCostCategory(
//                    recipe.getId(), spice.getId(), CostCategory.SPICES_OR_SEASONINGS);
//
//            // Update or create new entry based on existence
//            Costing spiceCost;
//            if (existingCostingOpt.isPresent()) {
//                spiceCost = existingCostingOpt.get();
//                spiceCost.setCost(costingDto.getCost());
//                spiceCost.setQuantity(costingDto.getQuantity());
//            } else {
//                spiceCost = new Costing();
//                spiceCost.setCommodityId(spice.getId());
//                spiceCost.setQuantity(costingDto.getQuantity());
//                spiceCost.setCost(costingDto.getCost());
//                spiceCost.setCostCategory(CostCategory.SPICES_OR_SEASONINGS);
//                spiceCost.setRecipeId(recipe.getId());
//                spiceCost.setDeletedFlag("N");
//            }
//
//            savedCost = costingRepo.save(spiceCost);
//        }
//
//        return savedCost;
//    }
public Costing saveFoodStockCost (CostingDto costingDto){
    Costing newCost = new Costing();
    newCost.setUnitPrice(costingDto.getUnitCost());
    Optional<FoodStock> optionalFoodStock = foodStockRepo.findByStockNumberAndDeletedFlagAndDepletedFlag(costingDto.getFoodSockNumber(),"N","N");
    if(optionalFoodStock.isPresent()){
        FoodStock foodStock = optionalFoodStock.get();
        newCost.setStockNumber(foodStock.getStockNumber());
    }

    newCost.setDate(costingDto.getDate());
return costingRepo.save(newCost);
}

    public List<Costing> findAllCosts() {
        logger.info("Fetching all costs");
        return costingRepo.findAll();
    }

    public Costing getCostById(Long id) {
        logger.info("Fetching cost with ID: {}", id);
        return costingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Costing not found with ID: " + id));
    }

    public Costing updateCost(Long id, CostingDto costingDto) {
        validateCostingDto(costingDto); // Validate input before updating

        logger.info("Updating cost with ID: {}", id);
        Costing existingCost = getCostById(id); // Reuse existing method

        existingCost.setQuantity(costingDto.getQuantity());
        existingCost.setUnitPrice(costingDto.getUnitCost());
        existingCost.setCostCategory(costingDto.getCostCategory());

        try {
            if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
                Optional<FoodStock> optionalIngredients =
                        foodStockRepo.findByIdAndDeletedFlagAndExpired(costingDto.getCommodityId(), "N", false);

                if (optionalIngredients.isPresent()) {
                    FoodStock ingredient = optionalIngredients.get();
                    existingCost.setCommodityId(ingredient.getId());
                } else {
                    logger.warn("Ingredient with ID {} not found or marked as deleted", costingDto.getCommodityId());
                    throw new IllegalArgumentException("Ingredient not found");
                }

            } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
                Optional<SpicesAndSeasonings> optionalSpicesAndSeasonings =
                        spicesRepo.findByIdAndDeletedFlag(costingDto.getCommodityId(), "N");

                if (optionalSpicesAndSeasonings.isPresent()) {
                    SpicesAndSeasonings spicesAndSeasonings = optionalSpicesAndSeasonings.get();
                    existingCost.setCommodityId(spicesAndSeasonings.getId());
                } else {
                    logger.warn("Spice or seasoning with ID {} not found or marked as deleted", costingDto.getCommodityId());
                    throw new IllegalArgumentException("Spice or seasoning not found");
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error updating cost: {}", e.getMessage());
            throw e; // rethrow to be handled by controller
        }

        logger.info("Cost updated successfully: {}", existingCost);
        return costingRepo.save(existingCost);
    }


    public void deleteCost(Long id) {
        logger.info("Deleting cost with ID: {}", id);
        Costing existingCost = getCostById(id); // Reuse existing method
        existingCost.setDeletedFlag("Y");
        costingRepo.save(existingCost); // Save the updated entity
        logger.info("Cost deleted with ID: {}", id);
    }

    // Helper methods
    private void validateCostingDto(CostingDto costingDto) {
        // Validate cost
        if (costingDto.getUnitCost() == null || costingDto.getUnitCost() <= 0) {
            throw new IllegalArgumentException("Cost must be greater than zero");
        }

        // Validate quantity
        if (costingDto.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity must not be null");
        }
    }

}

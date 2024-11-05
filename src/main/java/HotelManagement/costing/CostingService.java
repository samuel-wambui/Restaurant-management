package HotelManagement.costing;

import HotelManagement.ingredients.Ingredients;
import HotelManagement.ingredients.IngredientsRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostingService {
    private static final Logger logger = LoggerFactory.getLogger(CostingService.class);

    @Autowired
    private CostingRepo costingRepo;

    @Autowired
    private IngredientsRepo ingredientsRepo;

    @Autowired
    private SpicesAndSeasoningsRepo spicesRepo;

    public Costing saveCost(CostingDto costingDto) {
        validateCostingDto(costingDto); // Validate input

        // Create a new Costing instance
        Costing cost = new Costing();
        cost.setQuantity(costingDto.getQuantity());
        cost.setCost(costingDto.getCost());
        cost.setCostCategory(costingDto.getCostCategory());

        // Check and set the appropriate reference for ingredient or spice
        try {
            if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
                Ingredients ingredient = findIngredient(costingDto.getIngredientId());
                cost.setIngredient(ingredient);
            } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
                SpicesAndSeasonings spice = findSpice(costingDto.getSpiceId());
                cost.setSpicesAndSeasonings(spice);
            } else {
                throw new IllegalArgumentException("Invalid cost category");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error saving cost: {}", e.getMessage());
            throw e; // rethrow to be handled by controller
        }

        logger.info("Cost saved: {}", cost);
        // Save and return the new Costing entity
        return costingRepo.save(cost);
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
        existingCost.setCost(costingDto.getCost());
        existingCost.setCostCategory(costingDto.getCostCategory());

        try {
            if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
                Ingredients ingredient = findIngredient(costingDto.getIngredientId());
                existingCost.setIngredient(ingredient);
                existingCost.setSpicesAndSeasonings(null);
            } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
                SpicesAndSeasonings spice = findSpice(costingDto.getSpiceId());
                existingCost.setSpicesAndSeasonings(spice);
                existingCost.setIngredient(null);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error updating cost: {}", e.getMessage());
            throw e; // rethrow to be handled by controller
        }

        logger.info("Cost updated: {}", existingCost);
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
        if (costingDto.getCost() == null || costingDto.getCost() <= 0) {
            throw new IllegalArgumentException("Cost must be greater than zero");
        }

        // Validate quantity
        if (costingDto.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity must not be null");
        }

        // Parse quantity from String to Integer
        try {
            int quantity = Integer.parseInt(costingDto.getQuantity());
            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity must not be negative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity must be a valid number");
        }
    }

    private Ingredients findIngredient(Long ingredientId) {
        return ingredientsRepo.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with ID: " + ingredientId));
    }

    private SpicesAndSeasonings findSpice(Long spiceId) {
        return spicesRepo.findById(spiceId)
                .orElseThrow(() -> new IllegalArgumentException("Spice not found with ID: " + spiceId));
    }
}

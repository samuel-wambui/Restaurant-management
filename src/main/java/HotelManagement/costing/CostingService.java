package HotelManagement.costing;


import HotelManagement.stock.foodStock.FoodStock;
import HotelManagement.stock.foodStock.FoodStockRepo;
import HotelManagement.recipe.RecipeRepo;
import HotelManagement.stock.foodStock.spices.SpicesAndSeasonings;
import HotelManagement.stock.foodStock.spices.SpicesAndSeasoningsRepo;
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


public Costing saveFoodStockCost (CostingDto costingDto){
    Costing newCost = new Costing();
    newCost.setUnitPrice(costingDto.getUnitCost());
    Optional<FoodStock> optionalFoodStock = foodStockRepo.findByStockNumberAndDeletedFlagAndDepletedFlag(costingDto.getFoodSockNumber(),"N","N");
    if(optionalFoodStock.isPresent()){
        FoodStock foodStock = optionalFoodStock.get();
        newCost.setStockNumber(foodStock.getStockNumber());
    }
    newCost.setQuantity(costingDto.getQuantity());
    newCost.setTotalCost(costingDto.getTotalCost());
    newCost.setDiscount(costingDto.getDiscount());
    newCost.setCostCategory(CostCategory.INGREDIENT);


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

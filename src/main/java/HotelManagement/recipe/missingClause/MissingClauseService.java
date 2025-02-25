package HotelManagement.recipe.missingClause;

import HotelManagement.exemption.ResourceNotFoundException;
import HotelManagement.stock.foodStock.FoodStock;
import HotelManagement.stock.foodStock.FoodStockRepo;
import HotelManagement.recipe.todayRecipe.OrderedRecipe;
import HotelManagement.recipe.todayRecipe.OrderedRecipeRepo;
import HotelManagement.stock.foodStock.spices.SpicesAndSeasonings;
import HotelManagement.stock.foodStock.spices.SpicesAndSeasoningsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MissingClauseService {
    @Autowired
    MissingClauseRepo missingClauseRepo;

    @Autowired
    OrderedRecipeRepo orderedRecipeRepo;
    @Autowired
    FoodStockRepo foodStockRepo;
    @Autowired
    SpicesAndSeasoningsRepo spicesRepo;

    public MissingClauseRecipe createMissingClause(MissingClauseDto missingClauseDto) {
        MissingClauseRecipe missingClauseRecipe = new MissingClauseRecipe();
        missingClauseRecipe.setMissingClauseName(missingClauseDto.getMissingClauseName());

        // Retrieve and set FoodStock entities based on IDs
        Set<FoodStock> foodStockSet = new HashSet<>();
        for (Long foodStockId : missingClauseDto.getFoodStockSet()) {
            FoodStock foodStock = foodStockRepo.findById(foodStockId)
                    .orElseThrow(() -> new ResourceNotFoundException("FoodStock with ID " + foodStockId + " not found"));
            foodStockSet.add(foodStock);
        }
        missingClauseRecipe.setFoodStockSet(foodStockSet);

        // Retrieve and set SpicesAndSeasonings entities based on IDs
        Set<SpicesAndSeasonings> spicesSet = new HashSet<>();
        for (Long spiceId : missingClauseDto.getSpicesSet()) {
            SpicesAndSeasonings spice = spicesRepo.findById(spiceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Spice with ID " + spiceId + " not found"));
            spicesSet.add(spice);
        }

        missingClauseRecipe.setSpicesSet(spicesSet);

        // Check if OrderedRecipe exists
        OrderedRecipe orderedRecipe = orderedRecipeRepo.findById(missingClauseDto.getOrderedRecipe())
                .orElseThrow(() -> new ResourceNotFoundException("OrderedRecipe with ID " + missingClauseDto.getOrderedRecipe() + " not found"));
        missingClauseRecipe.getRecipes().add(orderedRecipe);

        return missingClauseRepo.save(missingClauseRecipe);
    }
}
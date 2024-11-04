package HotelManagement.costing;

import HotelManagement.ingredients.Ingredients;
import HotelManagement.ingredients.IngredientsRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostingService {
    @Autowired
    private CostingRepo costingRepo;
        @Autowired
        private IngredientsRepo ingredientsRepo;

        @Autowired
        private SpicesAndSeasoningsRepo spicesRepo;

    public Costing saveCost(CostingDto costingDto) {
        if (costingDto.getCost() == null || costingDto.getCost() <= 0) {
            throw new IllegalArgumentException("Cost must be greater than zero");
        }

        // Create a new Costing instance
        Costing cost = new Costing();
        cost.setQuantity(costingDto.getQuantity());
        cost.setCost(costingDto.getCost());
        cost.setCostCategory(costingDto.getCostCategory());

        // Check if the commodity is an ingredient or a spice and set the appropriate reference
        if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
            Ingredients ingredient = ingredientsRepo.findById(costingDto.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient: " + costingDto.getIngredientId() + " does not exist"));
            cost.setIngredient(ingredient);
        } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
            SpicesAndSeasonings spice = spicesRepo.findById(costingDto.getSpiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Specified spice does not exist"));
            cost.setSpice(spice);
        } else {
            throw new IllegalArgumentException("Invalid cost category");
        }

        // Save and return the new Costing entity
        return costingRepo.save(cost);
    }

    //find all
    public List<Costing> findAllCosts() {
        return costingRepo.findAll();
    }

    //find by id
    public Costing getCostById(Long id) {
        return costingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Costing not found with ID: " + id));
    }

    //update
    public Costing updateCost(Long id, CostingDto costingDto) {
        Costing existingCost = costingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Costing not found"));

        existingCost.setQuantity(costingDto.getQuantity());
        existingCost.setCost(costingDto.getCost());
        existingCost.setCostCategory(costingDto.getCostCategory());

        if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
            Ingredients ingredient = ingredientsRepo.findById(costingDto.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
            existingCost.setIngredient(ingredient);
            existingCost.setSpice(null);
        } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
            SpicesAndSeasonings spice = spicesRepo.findById(costingDto.getSpiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Spice not found"));
            existingCost.setSpice(spice);
            existingCost.setIngredient(null);
        }

        return costingRepo.save(existingCost);
    }
// delete
public void deleteCost(Long id) {
    Costing existingCost = costingRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Costing not found"));
    existingCost.setDeletedFlag("Y");
    costingRepo.save(existingCost); // Save the updated entity
}
}


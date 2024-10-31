package HotelManagement.costing;

import HotelManagement.ingredients.IngredientsRepo;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            // Check if the commodity exists based on its type (Ingredient or Spice)
            boolean commodityExists;
            if (costingDto.getCostCategory() == CostCategory.INGREDIENT) {
                commodityExists = ingredientsRepo.existsById(costingDto.getCommodityId());
            } else if (costingDto.getCostCategory() == CostCategory.SPICES_OR_SEASONINGS) {
                commodityExists = spicesRepo.existsById(costingDto.getCommodityId());
            } else {
                throw new IllegalArgumentException("Invalid cost category");
            }

            if (!commodityExists) {
                throw new IllegalArgumentException("Specified commodity does not exist");
            }

            Costing cost = new Costing();
            cost.setCommodityId(costingDto.getCommodityId());
            cost.setQuantity(costingDto.getQuantity());
            cost.setCost(costingDto.getCost());
            cost.setCostCategory(costingDto.getCostCategory());
            return costingRepo.save(cost);
        }
    }



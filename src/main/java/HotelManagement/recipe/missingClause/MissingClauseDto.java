package HotelManagement.recipe.missingClause;

import HotelManagement.foodStock.FoodStock;
import HotelManagement.spices.SpicesAndSeasonings;
import lombok.Data;

import java.util.Set;

@Data
public class MissingClauseDto {
    private String missingClauseName;
    private Set<FoodStock> foodStockSet;
    private Set<SpicesAndSeasonings> spicesSet;
    private Long orderedRecipe;
}

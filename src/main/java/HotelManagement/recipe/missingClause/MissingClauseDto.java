package HotelManagement.recipe.missingClause;

import HotelManagement.foodStock.FoodStock;
import HotelManagement.spices.SpicesAndSeasonings;
import lombok.Data;

import java.util.Set;

@Data
public class MissingClauseDto {
    private String missingClauseName;
    private Set<Long> foodStockSet;
    private Set<Long> spicesSet;
    private Long orderedRecipe;
}

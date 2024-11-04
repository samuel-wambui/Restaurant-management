package HotelManagement.recipe;

import lombok.Data;

@Data
public class RecipeSpiceIngredientCostDTO {
    private String recipeName;
    private String ingredientName;
    private Double ingredientCost;
    private String spiceName;
    private Double spiceCost;

    public RecipeSpiceIngredientCostDTO(String recipeName, String ingredientName, Double ingredientCost,
                                        String spiceName, Double spiceCost) {
        this.recipeName = recipeName;
        this.ingredientName = ingredientName;
        this.ingredientCost = ingredientCost;
        this.spiceName = spiceName;
        this.spiceCost = spiceCost;
    }
}


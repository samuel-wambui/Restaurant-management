package HotelManagement.recipe;

import HotelManagement.ingredients.IngredientDetailDTO;
import HotelManagement.spices.SpiceDetailDTO;

import java.util.List;

public class RecipeDetailsDTO {
    private String recipeName;
    private List<IngredientDetailDTO> ingredients;
    private List<SpiceDetailDTO> spices;
    private Double totalCost;


}
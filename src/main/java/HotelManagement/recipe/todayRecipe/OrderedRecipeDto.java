package HotelManagement.recipe.todayRecipe;

import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import lombok.Data;

import java.util.Set;

@Data
public class OrderedRecipeDto {
    private String orderedRecipeName;
    private Set<Recipe> recipes;
    private Set<MissingClauseRecipe> missingClauseRecipes;



}

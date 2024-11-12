package HotelManagement.recipe.todayRecipe;

import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.RecipeRepo;
import HotelManagement.recipe.missingClause.MissingClauseDto;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import HotelManagement.recipe.missingClause.MissingClauseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderedRecipeService {
    @Autowired
    RecipeRepo recipeRepo;
    @Autowired
    OrderedRecipeRepo orderedRecipeRepo;
    public OrderedRecipe createOrderedRecipe(OrderedRecipeDto orderedRecipeDto){
        OrderedRecipe orderedRecipe = new OrderedRecipe();
        orderedRecipe.setOrderedRecipeName(orderedRecipeDto.getOrderedRecipeName());
        orderedRecipe.setRecipeSet(orderedRecipeDto.getRecipes());
        orderedRecipe.setMissingClauseRecipes(orderedRecipeDto.getMissingClauseRecipes());
        return orderedRecipeRepo.save(orderedRecipe);
    }
}

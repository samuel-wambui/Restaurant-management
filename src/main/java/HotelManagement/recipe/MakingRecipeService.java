package HotelManagement.recipe;

import HotelManagement.ingredients.Ingredients;
import HotelManagement.ingredients.IngredientsRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class MakingRecipeService {

    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private IngredientsRepo ingredientsRepo;

    @Autowired
    private SpicesAndSeasoningsRepo spicesRepo;

    // Create a new recipe
    public Recipe addRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeDto.getRecipeName());

        Set<Ingredients> ingredientsSet = new HashSet<>();
        for (Long ingredientId : recipeDto.getIngredientIds()) {
            ingredientsRepo.findById(ingredientId).ifPresentOrElse(
                    ingredientsSet::add,
                    () -> { throw new IllegalArgumentException("Ingredient with ID " + ingredientId + " not found"); }
            );
        }

        Set<SpicesAndSeasonings> spicesSet = new HashSet<>();
        for (Long spiceId : recipeDto.getSpiceIds()) {
            spicesRepo.findById(spiceId).ifPresentOrElse(
                    spicesSet::add,
                    () -> { throw new IllegalArgumentException("Spice with ID " + spiceId + " not found"); }
            );
        }

        recipe.setIngredientsSet(ingredientsSet);
        recipe.setSpicesSet(spicesSet);

        return recipeRepo.save(recipe);
    }

    // Read a recipe by ID
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepo.findById(id);
    }

    // Read all recipes
    public Iterable<Recipe> getAllRecipes() {
        return recipeRepo.findAll();
    }

    // Update an existing recipe
    public Recipe updateRecipe(Long id, RecipeDto recipeDto) {
        Recipe recipe = recipeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe with ID " + id + " not found"));

        recipe.setRecipeName(recipeDto.getRecipeName());

        Set<Ingredients> ingredientsSet = new HashSet<>();
        for (Long ingredientId : recipeDto.getIngredientIds()) {
            Ingredients ingredient = ingredientsRepo.findById(ingredientId)
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient with ID " + ingredientId + " not found"));
            ingredientsSet.add(ingredient);
        }
        recipe.setIngredientsSet(ingredientsSet);

        Set<SpicesAndSeasonings> spicesSet = new HashSet<>();
        for (Long spiceId : recipeDto.getSpiceIds()) {
            SpicesAndSeasonings spice = spicesRepo.findById(spiceId)
                    .orElseThrow(() -> new IllegalArgumentException("Spice with ID " + spiceId + " not found"));
            spicesSet.add(spice);
        }
        recipe.setSpicesSet(spicesSet);

        return recipeRepo.save(recipe);
    }

    // Delete a recipe by ID
    public void deleteRecipe(Long id) {
        if (!recipeRepo.existsById(id)) {
            throw new IllegalArgumentException("Recipe with ID " + id + " not found");
        }
        recipeRepo.deleteById(id);
    }
}

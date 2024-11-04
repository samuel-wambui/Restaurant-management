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
        if (recipeDto.getIngredientIds() != null) {
            for (Long ingredientId : recipeDto.getIngredientIds()) {
                ingredientsRepo.findByIdAndDeletedFlag(ingredientId, "N").ifPresent(ingredientsSet::add);
            }
        }

        Set<SpicesAndSeasonings> spicesSet = new HashSet<>();
        if (recipeDto.getSpiceIds() != null) {
            for (Long spiceId : recipeDto.getSpiceIds()) {
                spicesRepo.findByIdAndDeletedFlag(spiceId, "N").ifPresent(spicesSet::add);
            }
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
        return (Iterable<Recipe>) recipeRepo.findAllByDeletedFlag("N");
    }

    // Update an existing recipe
    public Recipe updateRecipe(Long id, RecipeDto recipeDto) {
        // Find the recipe by ID and check if it's not deleted
        Optional<Recipe> optionalRecipe = recipeRepo.findByIdAndDeletedFlag(id, "N");
        if (!optionalRecipe.isPresent()) {
            throw new IllegalArgumentException("Recipe with ID " + id + " not found");
        }

        Recipe recipe = optionalRecipe.get();

        // Update recipe name
        recipe.setRecipeName(recipeDto.getRecipeName());

        // Update ingredients set
        Set<Ingredients> ingredientsSet = new HashSet<>();
        for (Long ingredientId : recipeDto.getIngredientIds()) {
            Optional<Ingredients> optionalIngredient = ingredientsRepo.findById(ingredientId);
            if (optionalIngredient.isPresent()) {
                ingredientsSet.add(optionalIngredient.get());
            } else {
                throw new IllegalArgumentException("Ingredient with ID " + ingredientId + " not found");
            }
        }
        recipe.setIngredientsSet(ingredientsSet);

        // Update spices set
        Set<SpicesAndSeasonings> spicesSet = new HashSet<>();
        for (Long spiceId : recipeDto.getSpiceIds()) {
            Optional<SpicesAndSeasonings> optionalSpice = spicesRepo.findById(spiceId);
            if (optionalSpice.isPresent()) {
                spicesSet.add(optionalSpice.get());
            } else {
                throw new IllegalArgumentException("Spice with ID " + spiceId + " not found");
            }
        }
        recipe.setSpicesSet(spicesSet);

        // Save and return the updated recipe
        return recipeRepo.save(recipe);
    }

    // Delete a recipe by ID
    public void deleteRecipe(Long id) {
        Recipe existingRecipe = recipeRepo.findById(id).get();
        if (existingRecipe == null) {
            throw new IllegalArgumentException("Recipe with ID " + id + " not found");
        }
        existingRecipe.setDeletedFlag("Y");

        recipeRepo.save(existingRecipe);
    }
}

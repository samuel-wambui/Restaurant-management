package HotelManagement.recipe;

import HotelManagement.costing.CostingService;
import HotelManagement.foodStock.FoodStock;
import HotelManagement.foodStock.FoodStockRepo;
import HotelManagement.spices.SpicesAndSeasonings;
import HotelManagement.spices.SpicesAndSeasoningsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MakingRecipeService {

    @Autowired
    private RecipeRepo recipeRepo;


    @Autowired
    private FoodStockRepo foodStockRepo;

    @Autowired
    private SpicesAndSeasoningsRepo spicesRepo;
    private static final Logger logger = LoggerFactory.getLogger(CostingService.class);
    // Create a new recipe
    public Recipe addRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeDto.getRecipeName());

        // Initialize sets to hold ingredients and spices
        Set<FoodStock> foodStockSet = new HashSet<>();
        Set<SpicesAndSeasonings> spicesSet = new HashSet<>();

        // Add ingredients based on provided IDs
        if (recipeDto.getIngredientIds() != null) {
            for (Long ingredientId : recipeDto.getIngredientIds()) {
                Optional<FoodStock> ingredientOpt = foodStockRepo.findByIdAndDeletedFlagAndExpired(ingredientId, "N", false);
                if (ingredientOpt.isPresent()) {
                    foodStockSet.add(ingredientOpt.get());
                } else {
                    logger.warn("Ingredient with ID {} not found or marked as deleted", ingredientId);
                }
            }
        }

        // Add spices based on provided IDs
        if (recipeDto.getSpiceIds() != null) {
            for (Long spiceId : recipeDto.getSpiceIds()) {
                Optional<SpicesAndSeasonings> spiceOpt = spicesRepo.findByIdAndDeletedFlag(spiceId, "N");
                if (spiceOpt.isPresent()) {
                    spicesSet.add(spiceOpt.get());
                } else {
                    logger.warn("Spice with ID {} not found or marked as deleted", spiceId);
                }
            }
        }

        // Set ingredients and spices sets in the Recipe
        recipe.setFoodStockSet(foodStockSet);
        recipe.setSpicesSet(spicesSet);

        // Save the recipe and return
        Recipe savedRecipe = recipeRepo.save(recipe);
        logger.info("Recipe saved with ID: {} and name: {}", savedRecipe.getId(), savedRecipe.getRecipeName());
        return savedRecipe;
    }



    // Read a recipe by ID
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepo.findById(id);
    }

    // Read all recipes
    public Iterable<Recipe> getAllRecipes() {
        return (Iterable<Recipe>) recipeRepo.findAllByDeletedFlag("N");
    }


//    public List<RecipeSpiceIngredientCostDTO> getAllRecipesWithIngredientsAndSpices() {
//        // Fetch projections from the repository
//        List<RecipeSpiceIngredientCostProjection> projections = recipeRepo.findAllRecipesWithIngredientsAndSpices();
//
//        // Convert projections to DTOs, ensuring proper aggregation by recipe ID
//        return RecipeSpiceIngredientCostDTO.fromProjections(projections);
//    }




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
        Set<FoodStock> foodStockSet = new HashSet<>();
        for (Long ingredientId : recipeDto.getIngredientIds()) {
            Optional<FoodStock> optionalIngredient = foodStockRepo.findById(ingredientId);
            if (optionalIngredient.isPresent()) {
                foodStockSet.add(optionalIngredient.get());
            } else {
                throw new IllegalArgumentException("Ingredient with ID " + ingredientId + " not found");
            }
        }
        recipe.setFoodStockSet(foodStockSet);

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

package HotelManagement.ingredients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientsService {

    @Autowired
    private IngredientsRepo ingredientsRepo;

    // Create a new ingredient
    public Ingredients createIngredient(IngredientsDto ingredientsDto) {
        Ingredients ingredient = new Ingredients();
        ingredient.setName(ingredientsDto.getName());
        return ingredientsRepo.save(ingredient);
    }

    // Get a specific ingredient by ID
    public Optional<Ingredients> getIngredientById(Long id) {
        return ingredientsRepo.findById(id);
    }

    // Get all ingredients
    public List<Ingredients> getAllIngredients() {
        return ingredientsRepo.findAll();
    }

    // Update an existing ingredient
    public Ingredients updateIngredient(Long id, Ingredients updatedIngredient) {
        return ingredientsRepo.findById(id).map(ingredient -> {
            ingredient.setName(updatedIngredient.getName());
            return ingredientsRepo.save(ingredient);
        }).orElseThrow(() -> new IllegalArgumentException("Ingredient with ID " + id + " not found."));
    }

    // Delete an ingredient by ID
    public void deleteIngredient(Long id) {
        if (!ingredientsRepo.existsById(id)) {
            throw new IllegalArgumentException("Ingredient with ID " + id + " not found.");
        }
        ingredientsRepo.deleteById(id);
    }
}

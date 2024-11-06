package HotelManagement.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long > {
    //List<Recipe> findAllByDeletedFlag(String deletedFlag);

    @Query("SELECT DISTINCT r FROM Recipe r " +
            "LEFT JOIN FETCH r.ingredientsSet i " +
            "LEFT JOIN FETCH r.spicesSet s " +
            "WHERE r.deletedFlag = :deletedFlag")
    List<Recipe> findAllByDeletedFlag(@Param("deletedFlag") String deletedFlag);
    @Query(value = "SELECT r.id AS recipeId, " +
            "r.recipe_name AS recipeName, " +

            // Ingredient information with quantity
            "GROUP_CONCAT(DISTINCT i.name) AS ingredientNames, " +
            "GROUP_CONCAT(DISTINCT c1.cost) AS individualIngredientCosts, " +
            "GROUP_CONCAT(DISTINCT c1.quantity) AS ingredientQuantities, " + // Ingredient quantities from costing table
            "SUM(DISTINCT c1.cost) AS totalIngredientCost, " +

            // Spice information with quantity
            "GROUP_CONCAT(DISTINCT s.name) AS spiceNames, " +
            "GROUP_CONCAT(DISTINCT c2.cost) AS individualSpiceCosts, " +
            "GROUP_CONCAT(DISTINCT c2.quantity) AS spiceQuantities, " + // Spice quantities from costing table
            "SUM(DISTINCT c2.cost) AS totalSpiceCost, " +

            // Total recipe cost as the sum of ingredient and spice costs
            "(SUM(DISTINCT c1.cost) + SUM(DISTINCT c2.cost)) AS recipeTotalCost " +

            "FROM recipe r " +

            "LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id " +
            "LEFT JOIN ingredients i ON ri.ingredient_id = i.id " +
            "LEFT JOIN costing c1 ON i.id = c1.commodity_id AND c1.cost_category = 'INGREDIENT' " +

            "LEFT JOIN recipe_spices rs ON r.id = rs.recipe_id " +
            "LEFT JOIN spices_and_seasonings s ON rs.spice_id = s.id " +
            "LEFT JOIN costing c2 ON s.id = c2.commodity_id AND c2.cost_category = 'SPICES_OR_SEASONINGS' " +

            "WHERE r.deleted_flag = 'N' " +
            "AND (i.deleted_flag = 'N' OR s.deleted_flag = 'N') " +

            "GROUP BY r.id, r.recipe_name",
            nativeQuery = true)
    List<RecipeSpiceIngredientCostProjection> findAllRecipesWithIngredientsAndSpices();







    Optional<Recipe> findByIdAndDeletedFlag(Long id, String deletedFlag);
}




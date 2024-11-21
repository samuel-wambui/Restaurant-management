package HotelManagement.recipe.costPerRequest;

import HotelManagement.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CostPerRequestRepo extends JpaRepository<CostPerRequest, Long> {
    @Query(value = "SELECT MAX(CAST(SUBSTRING(request_number, 4) AS UNSIGNED)) " +
            "FROM cost_per_request", nativeQuery = true)
    Integer findLastServiceNumber();
    @Query(value = "SELECT r.* FROM recipe r " +
            "JOIN recipe_food_stock rfs ON r.id = rfs.recipe_id " +
            "JOIN food_stock fs ON rfs.food_stock_id = fs.id " +
            "WHERE r.id = :recipeId", nativeQuery = true)
    Optional<Recipe> findByIdWithFoodStock(@Param("recipeId") Long recipeId);
}

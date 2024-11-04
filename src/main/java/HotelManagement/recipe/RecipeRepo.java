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

        @Query("SELECT new HotelManagement.recipe.RecipeSpiceIngredientCostDTO(" +
                "r.recipeName, " +
                "i.name, " +
                "c1.cost, " +
                "s.name, " +
                "c2.cost) " +
                "FROM Recipe r " +
                "JOIN r.ingredientsSet i " +
                "JOIN i.costings c1 " +
                "JOIN r.spicesSet s " +
                "JOIN s.costings c2 " +
                "WHERE r.deletedFlag = 'N'")
        List<RecipeSpiceIngredientCostDTO> findAllRecipesWithIngredientsAndSpices();

    Optional<Recipe> findByIdAndDeletedFlag(Long id, String deletedFlag);
}




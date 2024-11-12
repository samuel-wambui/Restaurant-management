package HotelManagement.recipe.todayRecipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodayRecipeRepo extends JpaRepository<OrderedRecipe,Long> {

}

package HotelManagement.recipe.todayRecipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderedRecipeRepo extends JpaRepository<OrderedRecipe,Long> {

    Optional<OrderedRecipe> findByIdAndDeletedFlag(Long id, String deletedFlag);
    List<OrderedRecipe> findAllByAndDeletedFlag(String deletedFlag);
}

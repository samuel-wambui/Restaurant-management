package HotelManagement.ingredients;

import HotelManagement.recipe.Recipe;
import HotelManagement.spices.SpicesAndSeasonings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientsRepo extends JpaRepository<Ingredients,Long> {
   List <Ingredients> findAllByDeletedFlag(String deletedFlag);
    Optional<Ingredients> findByIdAndDeletedFlag (Long id, String deletedFlag);
}

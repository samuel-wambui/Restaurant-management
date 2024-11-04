package HotelManagement.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long > {
    List<Recipe> findAllByDeletedFlag(String deletedFlag);
    Optional<Recipe> findByIdAndDeletedFlag (Long id, String deletedFlag);
}

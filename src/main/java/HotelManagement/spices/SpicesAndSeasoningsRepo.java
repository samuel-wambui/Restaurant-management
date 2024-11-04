package HotelManagement.spices;

import HotelManagement.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpicesAndSeasoningsRepo extends JpaRepository<SpicesAndSeasonings,Long> {
    List<SpicesAndSeasonings> findAllByDeletedFlag(String deletedFlag);
    Optional<SpicesAndSeasonings> findByIdAndDeletedFlag (Long id, String deletedFlag);
}

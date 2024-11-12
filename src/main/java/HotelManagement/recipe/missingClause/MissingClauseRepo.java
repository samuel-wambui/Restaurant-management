package HotelManagement.recipe.missingClause;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissingClauseRepo extends JpaRepository<MissingClauseRecipe,Long> {
    Optional<MissingClauseRecipe> findByIdAndDeletedFlag(Long id, String deletedFlag);
    Optional<MissingClauseRecipe> findAllByAndDeletedFlag(String deletedFlag);
}

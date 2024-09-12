package HotelManagement.meals;

import HotelManagement.meals.MealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MealRepository extends JpaRepository<MealEntity, Long> {
    Optional<MealEntity> findTopByOrderByIdDesc();
}

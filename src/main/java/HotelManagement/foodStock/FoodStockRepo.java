package HotelManagement.foodStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodStockRepo extends JpaRepository<FoodStock,Long> {
   List <FoodStock> findAllByDeletedFlag(String deletedFlag);
    Optional<FoodStock> findByIdAndDeletedFlag (Long id, String deletedFlag);
}

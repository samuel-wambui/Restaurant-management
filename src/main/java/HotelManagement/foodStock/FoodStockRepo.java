package HotelManagement.foodStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodStockRepo extends JpaRepository<FoodStock,Long> {
    List<FoodStock> findAllByDeletedFlagAndExpiredOrderByExpiryDateAsc(String deletedFlag, boolean expired);
    @Query("SELECT f FROM FoodStock f WHERE f.expiryDate BETWEEN :startDate AND :endDate AND f.expired = false")
    List<FoodStock> findItemsExpiringInRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<FoodStock> findByIdAndDeletedFlagAndExpired (Long id, String deletedFlag, boolean expired);
    @Query(value = "SELECT MAX(CAST(SUBSTRING(stock_number, 4) AS UNSIGNED)) " +
            "FROM food_stock", nativeQuery = true)
    Integer findLastServiceNumber();

    Optional<FoodStock> findByStockNumberAndDeletedFlagAndDepletedFlag(String stockNumber, String deletedFlag, String depletedFlag);

}

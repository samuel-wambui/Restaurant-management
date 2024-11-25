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

    Optional<FoodStock> findByStockNumber(String stockNumber);

    List<FoodStock> findByStockNameAndDepletedFlagAndDeletedFlag(String stockName, String depletedFlag, String deletedFlag);
    @Query(value = "SELECT SUM(c.quantity) AS total_quantity " +
            "FROM food_stock f " +
            "LEFT JOIN costing c ON c.stock_number = f.stock_number " +
            "WHERE f.stock_name = :name",
            nativeQuery = true)
    Double findTotalQuantityByName(@Param("name") String name);

    @Query(
            value = "SELECT MIN(id) AS id, stock_name AS stockName " +
                    "FROM food_stock " +
                    "WHERE deleted_flag = 'N' " +
                    "GROUP BY stock_name",
            nativeQuery = true
    )
    List<UniqueStockNameProjection> findUniqueStockName();


    @Query(value = "SELECT * " +
            "FROM food_stock " +
            "WHERE stock_name = :stockName AND depleted_flag = 'N' AND deleted_flag = 'N' " +
            "ORDER BY expiry_date ASC, id ASC",
            nativeQuery = true)
    List<FoodStock> findValidFoodStocks(@Param("stockName") String stockName);

    @Query(value = """
        SELECT 
            f.id AS id,
            f.stock_name AS stockName,
            f.stock_number AS stockNumber,
            f.unit_number AS unitNumber,
            COALESCE(c.quantity * u.unit, 0) AS quantityInUnits,
            u.unit_name AS unitName,
            COALESCE(c.quantity * u.sub_unit, 0) AS quantityInSubUnits,
            u.sub_unit_name AS subUnitName,
            c.unit_price AS unitPrice,
            c.total_cost AS totalCost,
            c.discount AS discount,
            f.expiry_date AS expiryDate
        FROM 
            food_stock f
        LEFT JOIN 
            costing c ON c.stock_number = f.stock_number
        LEFT JOIN 
            unit_measurement u ON f.unit_number = u.unit_measurement_number
        WHERE 
            f.depleted_flag = "N"
        ORDER BY 
            f.expiry_date ASC
        """, nativeQuery = true)
    List<FoodStockProjection> findFoodStockWithDetails();

}





package HotelManagement.costing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostingRepo extends JpaRepository<Costing, Long> {

    @Query("SELECT c FROM Costing c WHERE c.recipeId = :recipeId AND c.commodityId = :commodityId AND c.costCategory = :costCategory AND c.deletedFlag = 'N'")
    Optional<Costing> findByRecipeIdAndCommodityIdAndCostCategory(
            @Param("recipeId") Long recipeId,
            @Param("commodityId") Long commodityId,
            @Param("costCategory") CostCategory costCategory);

    boolean existsByRecipeId(Long recipeId);

    Optional<Costing> findByStockNumber(String foodStockNumber);
    @Query(value = """
            SELECT 
                f.id, 
                f.stock_name,
                f.stock_number,
                c.quantity,
                u.unit_name,
                c.discount,
                f.purchase_date,
                f.expiry_date,
                u.unit_measurement_number
            FROM 
                food_stock f
            LEFT JOIN 
                costing c ON f.stock_number = c.stock_number
            LEFT JOIN 
                unit_measurement u ON u.unit_measurement_number = f.unit_number
            ORDER BY
                f.expiry_date ASC
            """, nativeQuery = true)
    List<Object[]> findFoodStockWithDetails();
}


package HotelManagement.costing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CostingRepo extends JpaRepository<Costing, Long> {

    @Query("SELECT c FROM Costing c WHERE c.recipeId = :recipeId AND c.commodityId = :commodityId AND c.costCategory = :costCategory AND c.deletedFlag = 'N'")
    Optional<Costing> findByRecipeIdAndCommodityIdAndCostCategory(
            @Param("recipeId") Long recipeId,
            @Param("commodityId") Long commodityId,
            @Param("costCategory") CostCategory costCategory);

    boolean existsByRecipeId(Long recipeId);
}


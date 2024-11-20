package HotelManagement.recipe.costPerRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CostPerRequestRepo extends JpaRepository<CostPerRequest, Long> {
    @Query(value = "SELECT MAX(CAST(SUBSTRING(request_number, 4) AS UNSIGNED)) " +
            "FROM cost_per_request", nativeQuery = true)
    Integer findLastServiceNumber();
}

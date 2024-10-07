package HotelManagement.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfitMultiplierRepo extends JpaRepository<ProfitMultiplier, Long> {
    Optional<ProfitMultiplier> findByStockName(String stockName);
}

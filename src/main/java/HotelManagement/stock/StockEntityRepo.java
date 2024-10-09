package HotelManagement.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockEntityRepo extends JpaRepository <Stock, Long> {

}

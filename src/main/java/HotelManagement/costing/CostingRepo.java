package HotelManagement.costing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostingRepo extends JpaRepository<Costing, Long> {

}

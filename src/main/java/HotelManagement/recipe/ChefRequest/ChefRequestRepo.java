package HotelManagement.recipe.ChefRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChefRequestRepo extends JpaRepository <ChefRequest, Long> {
}

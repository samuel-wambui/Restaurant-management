package HotelManagement.manager;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<ManagerEntity, Long> {
    Optional<ManagerEntity> findByUsername(String username);
    Optional<ManagerEntity> findByEmail(String email);
}

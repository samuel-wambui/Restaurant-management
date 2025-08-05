package HotelManagement.apiIntergrationSecurity;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientAppRegister, UUID> {
    Optional<ClientAppRegister> findByAppKey(String appKey);
}

package HotelManagement.roles;

import HotelManagement.roles.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Override
    Optional<Role> findById(Long aLong);

    Optional<Role> findByName(String name);


    List<Role> findByDeletedByNull();



    Optional<Role> findByIdAndDeletedByNull(Long roleId);


}

package HotelManagement.Auth.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface
UserRepository extends JpaRepository<User, Long> {

   Optional<User> findByUsernameAndDeletedFlag(String username, String deletedFlag);

   boolean existsByUsernameAndDeletedFlag(String username, String deletedFlag);

   boolean existsByPhoneNumberAndDeletedFlag(String phoneNumber, String deletedFlag);

   boolean existsByEmailAndDeletedFlag(String email, String deletedFlag);

   Optional<User> findByIdAndDeletedFlag(Long id, String deletedFlag);

   List<User> findAllByDeletedFlag(String deletedFlag);

    Optional<Object> findByUsername(String username);

    boolean existsByUsername(String adminUsername);

    Optional<User>findByEmailAndDeletedFlag(String email, String deletedFlag);


}

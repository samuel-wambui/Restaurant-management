package HotelManagement.repository;

import HotelManagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

   Optional<Employee> findByUsernameAndDeletedFalse(String username);

   boolean existsByUsernameAndDeletedFalse(String username);

   boolean existsByPhoneNumberAndDeletedFalse(long phoneNumber);

   boolean existsByEmailAndDeletedFalse(String email);

   Optional<Employee> findByIdAndDeletedFalse(long id);

   List<Employee> findAllByDeletedFalse();


}
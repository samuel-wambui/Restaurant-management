package HotelManagement.Departments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DepartmentRepo extends JpaRepository<DepartmentEntity, Long> {
    @Query("SELECT d.departmentCode FROM DepartmentEntity d ORDER BY d.departmentCode DESC")
    String findLastDepartmentCode();


}
